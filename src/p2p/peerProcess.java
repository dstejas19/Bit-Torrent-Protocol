package p2p;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

public class peerProcess {
    public static commonProp commonProp;
    public static peerProp peerProp;
    public static int peerId;
    public ArrayList<peerProp> allPeers;
    public static ConcurrentHashMap<Integer, peerProp> peerMap = new ConcurrentHashMap<>();


    public static void main(String[] args) {

        peerProcess proc = new peerProcess();
        peerId = Integer.parseInt(args[0]);
        System.out.println(peerId);

        commonProp = new commonProp();

        commonProp.read();

        peerProp = new peerProp();

        proc.allPeers = peerProp.read(peerId);

        ListIterator<peerProp> itr = proc.allPeers.listIterator();

        Server server = new Server(peerProp.port);
        server.start();

        while(itr.hasNext()) {
            peerProp peer = itr.next();
            if(peer.hasFile) {
                peer.getBitfield().set(0, (int)commonProp.numPieces, true);
            }
            else {
                peer.setBitfield(new BitSet((int) commonProp.numPieces));
            }

            peerMap.put(peer.peerId, peer);

            Client client = new Client(peer);
            client.start();

        }






    }
}
