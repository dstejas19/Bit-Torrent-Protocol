package p2p;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class Utility {
    public static int verify(byte[] msg) {
        String header = new String(Arrays.copyOfRange(msg, 0, 18));
        int zeroBits = ByteBuffer.wrap(Arrays.copyOfRange(msg, 18, 28)).getInt();
        int peerId = ByteBuffer.wrap(Arrays.copyOfRange(msg, 28, 32)).getInt();

        if(Objects.equals(header, "P2PFILESHARINGPROJ") && zeroBits == 0 && peerProcess.peerMap.containsKey(peerId)) {
            return peerId;
        }

        return -1;
    }

    public static void combinePieces() {

    }
}
