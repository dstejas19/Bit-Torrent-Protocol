package p2p;

import messages.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
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
                    int messageType = (int)msg[4];

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
                        manageBitFieldMessage(msg);
                    }
                    else if(messageType == 5) {
                        manageBitFieldMessage(msg);
                    }
                    else if(messageType == 6) {
                        manageBitFieldMessage(msg);
                    }
                    else if(messageType == 7) {
                        manageBitFieldMessage(msg);
                    }

//                    System.out.println(msg);
//                    output.writeObject("To peer " + remotePeerId + ", from peer " + peerProcess.peerId);
//                    count++;
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

}
