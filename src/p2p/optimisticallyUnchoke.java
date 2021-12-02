package p2p;

import messages.chokeMessage;
import messages.unchokeMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class optimisticallyUnchoke extends Thread{
    public void run() {
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

        Random random = new Random();

        int index = random.nextInt(interested.size());

        peerProcess.peerMap.get(index).optimisticallySendFile = true;
        peerProcess.peerMap.get(index).piecesSent = 0;

        unchokeMessage um = new unchokeMessage();

        try {
            peerProcess.connectionMap.get(index).output.writeObject(um.message);
            peerProcess.connectionMap.get(index).output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
