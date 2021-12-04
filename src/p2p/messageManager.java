package p2p;

import messages.*;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class messageManager extends Thread {
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
        while (true) {
            if (!msgQ.isEmpty()) {
                try {
                    byte[] msg = (byte[]) msgQ.poll();
                    int messageType = msg[4];

                    if (messageType == 0) {
                        manageChokeMessage(msg);
                    } else if (messageType == 1) {
                        manageUnchokeMessage(msg);
                    } else if (messageType == 2) {
                        manageInterestedMessage(msg);
                    } else if (messageType == 3) {
                        manageNotInterestedMessage(msg);
                    } else if (messageType == 4) {
                        manageHaveMessage(msg);
                    } else if (messageType == 5) {
                        manageBitFieldMessage(msg);
                    } else if (messageType == 6) {
                        manageRequestMessage(msg);
                    } else if (messageType == 7) {
                        managePieceMessage(msg);
                    } else {
                        synchronized (this) {
                            try {
                                wait();
                            } catch (Exception e) {
                                System.out.println("Message type exception - " + e);
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException a) {
                    a.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Message manager Exception - " + e);
                    e.printStackTrace();
                }
            }
        }
    }

    public void manageBitFieldMessage(byte[] msg) {
        BitSet receivedBitField = BitSet.valueOf(ByteBuffer.wrap(Arrays.copyOfRange(msg, 5, msg.length)));
        peerProcess.peerMap.get(remotePeerId).setBitfield(receivedBitField);
        BitSet currentBitField = (BitSet) peerProcess.peerProperty.getBitfield().clone();

        currentBitField.flip(0, (int) peerProcess.commonProperty.numPieces);
        currentBitField.and(receivedBitField);

        try {
            if (currentBitField.isEmpty()) {
                notinterestedMessage nim = new notinterestedMessage();
                output.writeObject(nim.message);
            } else {
                interestedMessage im = new interestedMessage();
                output.writeObject(im.message);
            }
            output.flush();
        } catch (Exception e) {
            System.out.println("Bitfield manager Exception - " + e);
        }
    }

    public void manageInterestedMessage(byte[] msg) {
//        System.out.println("Received interested message");
        peerProcess.log.info("Peer "+ peerProcess.peerId +" received the 'interested' message from " + remotePeerId);
        peerProcess.interestedPeers.put(remotePeerId, peerProcess.peerMap.get(remotePeerId));
    }

    public void manageNotInterestedMessage(byte[] msg) {
        peerProcess.interestedPeers.remove(remotePeerId);
        peerProcess.log.info("Peer "+ peerProcess.peerId +" received the 'uninterested' message from " + remotePeerId);
    }

    public void managePieceMessage(byte[] msg) throws FileNotFoundException {
//        System.out.println("received piece message");
        int payloadLength = ByteBuffer.wrap(Arrays.copyOfRange(msg, 0, 4)).getInt();
//        double start = peerProcess.peerMap.get(remotePeerId).start;
//
//        peerProcess.peerMap.get(remotePeerId).downloadRate = ((double) msgLength) / (System.nanoTime() - start);

        int pieceIndex = ByteBuffer.wrap(Arrays.copyOfRange(msg, 5, 9)).getInt();

        if (!peerProcess.peerProperty.bitfield.get(pieceIndex)) {
            byte[] filePieces = new byte[payloadLength - 5];

            if (msg.length - 9 >= 0) System.arraycopy(msg, 9, filePieces, 0, msg.length - 9);

            OutputStream outputStream = new FileOutputStream(peerProcess.commonProperty.fileDir + File.separator + pieceIndex + ".part");
            try {
                outputStream.write(filePieces);
                outputStream.close();
            } catch (IOException e) {
                System.out.println("Piece file exception - " + e);
            }

            peerProcess.peerProperty.bitfield.set(pieceIndex);

            peerProcess.log.info("Peer"+peerProcess.peerId+ " downloaded a piece "+pieceIndex+"from "+remotePeerId);
            haveMessage hm = new haveMessage(Arrays.copyOfRange(msg, 5, 9));
            Collection<connectionManager> connections = peerProcess.connectionMap.values();
            for (connectionManager connection : connections) {
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

        if (peerProcess.peerProperty.bitfield.cardinality() == peerProcess.commonProperty.numPieces) {
            List<byte[]> bytesList = new ArrayList<>();

            try {
                for (long i = 0; i < peerProcess.commonProperty.numPieces; i++) {
                    bytesList.add(Files.readAllBytes(new File(peerProcess.commonProperty.fileDir + File.separator + i + ".part").toPath()));
                }

                FileOutputStream fos = new FileOutputStream(peerProcess.commonProperty.fileDir + File.separator + peerProcess.commonProperty.fileName);

                for (byte[] data : bytesList) {
                    fos.write(data);
                }
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            peerProcess.peerProperty.hasFile = true;
            peerProcess.log.info("Peer "+peerProcess.peerId+"has finished downloading.");

            notinterestedMessage nim = new notinterestedMessage();
            Collection<connectionManager> connections = peerProcess.connectionMap.values();
            for (connectionManager connection : connections) {
                try {
                    connection.output.writeObject(nim.message);
                    connection.output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            sendRequestMessage();
        }

    }

    public synchronized void sendRequestMessage() {
//        System.out.println("send request message");
        if (peerProcess.peerMap.get(remotePeerId).receiveFile) {
//            peerProcess.peerMap.get(remotePeerId).start = System.nanoTime();

            BitSet currentBitField = (BitSet) peerProcess.peerProperty.getBitfield().clone();
            BitSet senderBitField = (BitSet) peerProcess.peerMap.get(remotePeerId).bitfield.clone();

            currentBitField.flip(0, (int) peerProcess.commonProperty.numPieces);
            currentBitField.and(senderBitField);

            if (!currentBitField.isEmpty()) {
                for (int i = currentBitField.nextSetBit(0); i < peerProcess.commonProperty.numPieces; i = currentBitField.nextSetBit(i + 1)) {
                    requestMessage rm = new requestMessage(i);
                    try {
                        output.writeObject(rm.message);
                        output.flush();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void manageHaveMessage(byte[] msg) {
//        System.out.println("Received have message");
//        System.out.println("Received have message");
        int pieceIndex = ByteBuffer.wrap(Arrays.copyOfRange(msg, 5, 9)).getInt();

        peerProcess.peerMap.get(remotePeerId).bitfield.set(pieceIndex);
        if (!peerProcess.peerProperty.getBitfield().get(pieceIndex)) {
            interestedMessage im = new interestedMessage();
            try {
                output.writeObject(im.message);
                output.flush();
                peerProcess.log.info("Peer "+peerProcess.peerId+"received the 'have' message from "+remotePeerId+"for piece "+pieceIndex);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void manageRequestMessage(byte[] msg) {
//        System.out.println("Received request message");
        if (peerProcess.peerMap.get(remotePeerId).sendFile || peerProcess.peerMap.get(remotePeerId).optimisticallySendFile) {
            int pieceIndex = ByteBuffer.wrap(Arrays.copyOfRange(msg, 5, 9)).getInt();
            peerProcess.peerMap.get(remotePeerId).piecesSent += 1;
//            System.out.println("Error hererererererererererererererererererere" + pieceIndex);
            try {
                byte[] pieceFile = Files.readAllBytes(new File(peerProcess.commonProperty.fileDir + File.separator + pieceIndex + ".part").toPath());
                pieceMessage pm = new pieceMessage(pieceIndex, pieceFile);
                output.writeObject(pm.message);
                output.flush();

//                System.out.println("Messssageeeeeeeee sennttttttttttttt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void manageUnchokeMessage(byte[] msg) {
        peerProcess.log.info("Peer " +peerProcess.peerId + "is unchoked by " + remotePeerId);
        peerProcess.peerMap.get(remotePeerId).receiveFile = true;
        sendRequestMessage();
    }

    public void manageChokeMessage(byte[] msg) {
        peerProcess.log.info("Peer " +peerProcess.peerId + "is choked by " + remotePeerId);
        peerProcess.peerMap.get(remotePeerId).receiveFile = false;
        System.out.println("Received choke message");
    }
}

