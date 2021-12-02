package messages;

import java.nio.ByteBuffer;

public class pieceMessage {
    byte[] messageLength;
    byte messageType = (byte) 7;
    public byte[] message;

    public pieceMessage(byte[] pieceIndex) {
        messageLength = ByteBuffer.allocate(4).putInt(pieceIndex.length+1).array();
        message = ByteBuffer.allocate(messageLength.length + 4).array();

        int i = 0;

        for(int c=0;c<4;c++) {
            message[i] = messageLength[c];
            i++;
        }

        message[i] = messageType;
        i++;

        for(int c=0;c<pieceIndex.length;c++) {
            message[i] = pieceIndex[c];
            i++;
        }
    }
}
