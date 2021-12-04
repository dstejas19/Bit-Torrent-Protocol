package p2p;

import messages.unchokeMessage;
import messages.chokeMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class chokeUnchoke extends  Thread{
    public void run() {
        while(true) {
            String strmsg="Peer" +peerProcess.peerId+"has the preferred neighbors";
            try {
                Thread.sleep(peerProcess.commonProperty.unchokingInterval * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<peerProp> interested = new ArrayList<>();
            for(peerProp peer:peerProcess.interestedPeers.values()) {
                interested.add(peerProcess.peerMap.get(peer.peerId));
            }

            if(interested.size() == 0) {
                continue;
            }

            int count = 0;

            if(peerProcess.peerProperty.hasFile) {
                while (count < peerProcess.commonProperty.numNeighbours) {
                    if(interested.size() == 0) {
                        break;
                    }

                    Random random = new Random();

                    int index = random.nextInt(interested.size());

                    if(interested.get(index).sendFile) {
                        peerProcess.peerMap.get(interested.get(index).peerId).piecesSent = 0;
                    }
                    else {
                        peerProcess.peerMap.get(interested.get(index).peerId).sendFile = true;
                        unchokeMessage um = new unchokeMessage();


                        try {
                            peerProcess.connectionMap.get(interested.get(index).peerId).output.writeObject(um.message);
                            peerProcess.connectionMap.get(interested.get(index).peerId).output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    peerProcess.log.info(strmsg+ interested.get(index).peerId + " ");

                    interested.remove(index);
                    count++;
                }

                for(peerProp peer:interested) {
                    if(!peerProcess.peerMap.get(peer.peerId).optimisticallySendFile) {
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
            else {
                Collections.sort(interested, Comparator.comparingInt(peerProp::getPiecesSent).reversed());

                for(peerProp peer: interested) {
                    if(count < peerProcess.commonProperty.numNeighbours) {
                        if(!peer.sendFile) {
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

                        peerProcess.log.info(strmsg+ peer.peerId);
                    }
                    else {
                        if(!peerProcess.peerMap.get(peer.peerId).optimisticallySendFile) {
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
    }
}
