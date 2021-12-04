package p2p;

import messages.chokeMessage;
import messages.unchokeMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class optimisticallyUnchoke extends Thread{
    public void run() {
        while(true) {
            try {
                Thread.sleep(peerProcess.commonProperty.optunchokingInterval * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<peerProp> interested = new ArrayList<>();
            for(peerProp peer:peerProcess.interestedPeers.values()) {
                if(peerProcess.peerMap.get(peer.peerId).optimisticallySendFile) {
                    peerProcess.peerMap.get(peer.peerId).optimisticallySendFile = false;
                    chokeMessage cm = new chokeMessage();
                    try {
                        peerProcess.connectionMap.get(peer.peerId).output.writeObject(cm.message);
                        peerProcess.connectionMap.get(peer.peerId).output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(!peerProcess.peerMap.get(peer.peerId).sendFile) {
                    interested.add(peerProcess.peerMap.get(peer.peerId));
                }
            }

            if(interested.size() == 0) {
                continue;
            }

            Random random = new Random();

            int index = random.nextInt(interested.size());

            peerProcess.peerMap.get(interested.get(index).peerId).optimisticallySendFile = true;
            peerProcess.peerMap.get(interested.get(index).peerId).piecesSent = 0;

            unchokeMessage um = new unchokeMessage();
            peerProcess.log.info("Peer " + peerProcess.peerId + "has optimistically unchoked neighbor "+interested.get(index).peerId);
            try {
                peerProcess.connectionMap.get(interested.get(index).peerId).output.writeObject(um.message);
                peerProcess.connectionMap.get(interested.get(index).peerId).output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
