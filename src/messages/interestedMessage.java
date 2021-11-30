package messages;

import java.nio.ByteBuffer;

public class interestedMessage {
    byte[] messageLength;
    byte messageType = (byte) 2;
    public byte[] message;

    public interestedMessage() {
        messageLength = ByteBuffer.allocate(4).putInt(1).array();
        message = ByteBuffer.allocate(5).array();

        for(int i=0;i<4;i++) {
            message[i] = messageLength[i];
        }

        message[4] = messageType;
    }
}
