package p2p;

import java.io.IOException;

public class End extends Thread{
    public void run() {
        while(true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean canEnd = peerProcess.peerProperty.hasFile && peerProcess.peerProperty.bitfield.cardinality() == peerProcess.commonProperty.numPieces;

            System.out.println("First " + canEnd);
            if (!canEnd) {
                continue;
            }

            for (peerProp peer : peerProcess.peerMap.values()) {
                System.out.println("Cardinalty and peerid " + peer.bitfield.cardinality() + peer.peerId);
                if (peer.bitfield.cardinality() != peerProcess.commonProperty.numPieces) {
                    canEnd = false;
                    break;
                }
            }

            System.out.println("Second " + canEnd);

            if (!canEnd) {
                continue;
            }

            for (connectionManager cm : peerProcess.connectionMap.values()) {
                try {
                    cm.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.exit(0);
        }
    }
}
