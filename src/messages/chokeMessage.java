package messages;

import java.nio.ByteBuffer;

public class chokeMessage {
    byte[] messageLength;
    byte messageType = (byte) 0;
    public byte[] message;

    public chokeMessage() {
        messageLength = ByteBuffer.allocate(4).putInt(1).array();
        message = ByteBuffer.allocate(4+1).array();

        int i = 0;

        for(int c=0;c<4;c++) {
            message[i] = messageLength[c];
            i++;
        }

        message[i] = messageType;
    }
}
