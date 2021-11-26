package messages;

import p2p.peerProcess;

import java.nio.ByteBuffer;

public class handshakeMessage {
    String header = "P2PFILESHARINGPROJ";
    byte[] headerBytes = new byte[18];
    byte[] zeroBits = new byte[10];
    byte[] peerId = new byte[4];
    public byte[] message = new byte[32];

    public handshakeMessage() {
        zeroBits = ByteBuffer.allocate(10).putInt(0).array();
        peerId = ByteBuffer.allocate(4).putInt(peerProcess.peerId).array();
        headerBytes = header.getBytes();

        int i = 0;

        for(int c=0;c<18;c++) {
            message[i] = headerBytes[c];
            i++;
        }

        for(int c=0;c<10;c++) {
            message[i] = zeroBits[c];
            i++;
        }

        for(int c=0;c<4;c++) {
            message[i] = peerId[c];
            i++;
        }
    }
}
