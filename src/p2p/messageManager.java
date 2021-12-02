package p2p;

import messages.*;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class messageManager extends Thread{
    Socket socket;
    ObjectInputStream input;
    ObjectOutputStream output;
    int remotePeerId;
    public ConcurrentLinkedQueue<Object> msgQ = new ConcurrentLinkedQueue<Object>();

    public messageManager(Socket socket, ObjectInputStream in, ObjectOutputStream op, int id) {
        this.socket = socket;
        input = in;
        output = op;
        remotePeerId = id;
    }

    public void run() {
        int count = 0;
        while(count < 5) {
            if(!msgQ.isEmpty()) {
                try {
                    byte[] msg = (byte[]) msgQ.poll();
                    int messageType = msg[4];

                    if(messageType == 0) {

                    }
                    else if(messageType == 1) {
                        manageBitFieldMessage(msg);
                    }
                    else if(messageType == 2) {
                        manageInterestedMessage(msg);
                    }
                    else if(messageType == 3) {
                        manageNotInterestedMessage(msg);
                    }
                    else if(messageType == 4) {
                        manageHaveMessage(msg);
                    }
                    else if(messageType == 5) {
                        manageBitFieldMessage(msg);
                    }
                    else if(messageType == 6) {
                        manageRequestMessage(msg);
                    }
                    else if(messageType == 7) {
                        managePieceMessage(msg);
                    }
                    else {
                        synchronized (this) {
                            try {
                                wait();
                            } catch (Exception e) {
                                System.out.println("Message type exception - " + e);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Message manager Exception - " + e);
                }
            }
        }
    }

    public void manageBitFieldMessage(byte[] msg) {
        BitSet receivedBitField = BitSet.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(msg, 5, msg.length)));
        peerProcess.peerMap.get(remotePeerId).setBitfield(receivedBitField);

        System.out.println("Received bitfield from " + remotePeerId + " " + receivedBitField);

        BitSet currentBitField = (BitSet) peerProcess.peerProperty.getBitfield().clone();

        currentBitField.flip(0, (int) peerProcess.commonProperty.numPieces);
        currentBitField.and(receivedBitField);

        try {
            if(currentBitField.isEmpty()) {
                notinterestedMessage nim = new notinterestedMessage();
                output.writeObject(nim.message);
            }
            else {
                interestedMessage im = new interestedMessage();
                output.writeObject(im.message);
            }
            output.flush();
        } catch(Exception e) {
            System.out.println("Bitfield manager Exception - " + e);
        }
    }

    public void manageInterestedMessage(byte[] msg) {
        peerProcess.interestedPeers.put(remotePeerId, peerProcess.peerMap.get(remotePeerId));
    }

    public void manageNotInterestedMessage(byte[] msg) {
        peerProcess.interestedPeers.remove(remotePeerId);
        System.out.println("Received not interested message");
    }

    public void managePieceMessage(byte[] msg) throws FileNotFoundException {
        int msgLength = ByteBuffer.wrap(Arrays.copyOfRange(msg, 0, 4)).getInt();
        double start = peerProcess.peerMap.get(remotePeerId).start;

        peerProcess.peerMap.get(remotePeerId).downloadRate = ((double) msgLength)/(System.nanoTime() - start);

        int pieceIndex = ByteBuffer.wrap(Arrays.copyOfRange(msg, 5, 9)).getInt();

        if(!peerProcess.peerProperty.bitfield.get(pieceIndex)) {
            byte[] filePieces = new byte[msgLength - 4];

            if (msg.length - 9 >= 0) System.arraycopy(msg, 9, filePieces, 0, msg.length - 9);

            OutputStream outputStream = new FileOutputStream(peerProcess.commonProperty.fileDir + File.separator + pieceIndex + ".part");
            try {
                outputStream.write(filePieces);
                outputStream.close();
            } catch (IOException e) {
                System.out.println("Piece file exception - " + e);
            }

            peerProcess.peerProperty.bitfield.set(pieceIndex);

            haveMessage hm = new haveMessage(Arrays.copyOfRange(msg, 5, 9));
            Collection<connectionManager> connections = peerProcess.connectionMap.values();
            for(connectionManager connection : connections) {
                try {
                    connection.output.writeObject(hm.message);
                    connection.output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        BitSet currentBitset = (BitSet) peerProcess.peerProperty.bitfield.clone();
        currentBitset.flip(0, (int) peerProcess.commonProperty.numPieces);

        if(currentBitset.isEmpty() && currentBitset.cardinality() == peerProcess.commonProperty.numPieces) {
            List<byte[]> bytesList = new ArrayList<>();

            try {
                for(long i=0;i<peerProcess.commonProperty.numPieces;i++) {
                    bytesList.add(Files.readAllBytes(new File(peerProcess.commonProperty.fileDir + File.separator + i + ".part").toPath()));
                }

                FileOutputStream fos = new FileOutputStream(peerProcess.commonProperty.fileDir + File.separator + peerProcess.commonProperty.fileName);

                for (byte[] data: bytesList) {
                    fos.write(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            sendRequestMessage();
        }

    }

    public synchronized  void sendRequestMessage() {
        if(!peerProcess.peerMap.get(remotePeerId).choked) {
            peerProcess.peerMap.get(remotePeerId).start = System.nanoTime();

            BitSet currentBitField = (BitSet) peerProcess.peerProperty.getBitfield().clone();
            BitSet senderBitField = (BitSet) peerProcess.peerMap.get(remotePeerId).bitfield.clone();

            currentBitField.flip(0, (int) peerProcess.commonProperty.numPieces);
            currentBitField.and(senderBitField);

            if(!currentBitField.isEmpty()) {
                for(int i=currentBitField.nextSetBit(0);i<peerProcess.commonProperty.numPieces;i=currentBitField.nextSetBit(i+1)) {
                    requestMessage rm = new requestMessage(i);
                    try {
                        output.writeObject(rm.message);
                        output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void manageHaveMessage(byte[] msg) {
        System.out.println("Received have message");
        int pieceIndex = ByteBuffer.wrap(Arrays.copyOfRange(msg, 5, 9)).getInt();
    
        peerProcess.peerMap.get(remotePeerId).bitfield.set(pieceIndex);
        if (!peerProcess.peerProperty.getBitfield().get(pieceIndex))
        {
            interestedMessage im = new interestedMessage();
            try {
                output.writeObject(im.message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }

    }
    public void manageRequestMessage (byte[] msg)
    {


        if(peerProcess.peerMap.get(remotePeerId).sendFile || peerProcess.peerMap.get(remotePeerId).optimisticallySendFile)
        {
        int pieceIndex = ByteBuffer.wrap(Arrays.copyOfRange(msg, 5, 9)).getInt();


        try {
        byte[] piecefile = Files.readAllBytes(new File(peerProcess.commonProperty.fileDir + File.separator + pieceIndex + ".part").toPath());
        pieceMessage pm= new pieceMessage(piecefile);
        output.writeObject(pm.message);
        output.flush();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
}

