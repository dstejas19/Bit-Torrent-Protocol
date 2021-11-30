package p2p;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

public class peerProcess {
    public static commonProp commonProperty;
    public static peerProp peerProperty;
    public static int peerId;
    public ArrayList<peerProp> allPeers;
    public static ConcurrentHashMap<Integer, peerProp> peerMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, peerProp> interestedPeers = new ConcurrentHashMap<>();


    public static void main(String[] args) {
        peerProcess proc = new peerProcess();
        peerId = Integer.parseInt(args[0]);
        System.out.println(peerId);

        commonProperty = new commonProp();

        commonProperty.read();

        peerProperty = new peerProp();

        proc.allPeers = peerProperty.read(peerId);

        if(peerProcess.peerProperty.hasFile) {
            peerProcess.peerProperty.setBitfield(new BitSet((int) commonProperty.numPieces));
            peerProcess.peerProperty.getBitfield().set(0, (int) commonProperty.numPieces, true);
        }
        else {
            peerProcess.peerProperty.setBitfield(new BitSet((int) commonProperty.numPieces));
        }

//        System.out.println(peerProcess.peerProperty.getBitfield());

        Server server = new Server(peerProperty.port);
        server.start();

//        System.out.println(peerProcess.peerProperty.getBitfield());

        ListIterator<peerProp> itr = proc.allPeers.listIterator();

        while(itr.hasNext()) {
            peerProp peer = itr.next();
//            System.out.println(peer.peerId);
//            System.out.println(peerProcess.peerProperty.getBitfield());
            if(peer.hasFile) {
                peer.setBitfield(new BitSet((int) commonProperty.numPieces));
                peer.getBitfield().set(0, (int) commonProperty.numPieces, true);
            }
            else {
                peer.setBitfield(new BitSet((int) commonProperty.numPieces));
            }

            peerMap.put(peer.peerId, peer);

            Client client = new Client(peer);
            client.start();

        }

//        System.out.println(peerProcess.peerProperty.getBitfield());
    }
}
