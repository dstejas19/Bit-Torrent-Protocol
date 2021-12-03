package messages;

import p2p.peerProcess;

import java.nio.ByteBuffer;

public class pieceMessage {
    byte[] messageLength;
    byte messageType = (byte) 7;
    public byte[] message;

    public pieceMessage(int pieceIndex, byte[] pieceFile) {
        byte[] pieceIndexPayload = ByteBuffer.allocate(4).putInt(pieceIndex).array();
        messageLength = ByteBuffer.allocate(4).putInt(pieceFile.length+1+4).array();
        message = ByteBuffer.allocate(pieceFile.length + 1 + 4 + 4).array();

        int i = 0;

        for(int c=0;c<4;c++) {
            message[i] = messageLength[c];
            i++;
        }

        message[i] = messageType;
        i++;

        for(int c=0;c<4;c++) {
            message[i] = pieceIndexPayload[c];
            i++;
        }

        for (byte b : pieceFile) {
            message[i] = b;
            i++;
        }


    }
}
