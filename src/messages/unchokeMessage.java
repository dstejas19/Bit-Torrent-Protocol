package messages;

import p2p.peerProcess;

import java.nio.ByteBuffer;

public class unchokeMessage {
    byte[] messageLength;
    byte messageType = (byte) 1;
    public byte[] message;

    public unchokeMessage() {
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
