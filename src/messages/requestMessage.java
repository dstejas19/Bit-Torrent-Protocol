package messages;

import java.nio.ByteBuffer;

public class requestMessage {
    byte[] messageLength;
    byte messageType = (byte) 6;
    public byte[] message;

    public requestMessage(int pieceIndex) {
        byte[] payload = ByteBuffer.allocate(4).putInt(pieceIndex).array();
        messageLength = ByteBuffer.allocate(4).putInt(5).array();
        message = ByteBuffer.allocate(9).array();

        int i = 0;

        for(int c=0;c<4;c++) {
            message[i] = messageLength[c];
            i++;
        }

        message[i] = messageType;
        i++;

        for(int c=0;c<4;c++) {
            message[i] = messageLength[c];
            i++;
        }
    }
}
