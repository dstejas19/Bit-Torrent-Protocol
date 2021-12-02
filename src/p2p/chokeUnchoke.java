package p2p;

import messages.unchokeMessage;
import messages.chokeMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class chokeUnchoke extends  Thread{
    public void run() {
        ArrayList<peerProp> interested = new ArrayList<>();
        for(peerProp peer:peerProcess.interestedPeers.values()) {
            interested.add(peerProcess.peerMap.get(peer.peerId));
        }
        Collections.sort(interested, Comparator.comparingInt(peerProp::getPiecesSent).reversed());

        int count = 0;

        for(peerProp peer: interested) {
            if(count < peerProcess.commonProperty.numNeighbours) {
                if(!peer.sendFile) {
                    //send unchoke message
                    unchokeMessage um = new unchokeMessage();

                    try {
                        peerProcess.connectionMap.get(peer.peerId).output.writeObject(um.message);
                        peerProcess.connectionMap.get(peer.peerId).output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                peerProcess.peerMap.get(peer.peerId).piecesSent = 0;
                peerProcess.peerMap.get(peer.peerId).sendFile = true;
                count++;
            }
            else {
                if(!peerProcess.peerMap.get(peer.peerId).optimisticallySendFile) {
                    // send choke message
                    chokeMessage cm = new chokeMessage();

                    try {
                        peerProcess.connectionMap.get(peer.peerId).output.writeObject(cm.message);
                        peerProcess.connectionMap.get(peer.peerId).output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    peerProcess.peerMap.get(peer.peerId).sendFile = false;
                }
            }
        }
    }
}
