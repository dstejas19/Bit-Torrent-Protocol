package messages;

import p2p.peerProcess;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class bitfieldMessage {
    byte[] messageLength;
    byte messageType = (byte) 5;
    public byte[] message;
//    public byte[] message = new byte[32];

    public bitfieldMessage() {
        byte[] payload = peerProcess.peerProperty.getBitfield().toByteArray();
        messageLength = ByteBuffer.allocate(4).putInt(payload.length+1).array();
        message = ByteBuffer.allocate(4+1+payload.length).array();

        int i = 0;

        for(int c=0;c<4;c++) {
            message[i] = messageLength[c];
            i++;
        }

        message[i] = messageType;
        i++;

        for(int c=0;c<payload.length;c++) {
            message[i] = payload[c];
            i++;
        }
    }
}
