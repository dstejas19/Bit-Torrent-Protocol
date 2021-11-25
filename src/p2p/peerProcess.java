package p2p;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.ListIterator;

public class peerProcess {
    commonProp commonProp;
    peerProp peerProp;
    public static int peerId;
    ArrayList<peerProp> neighbours;

    public static void main(String[] args) {

        peerProcess proc = new peerProcess();
        proc.peerId = Integer.parseInt(args[0]);
        System.out.println(proc.peerId);

        proc.commonProp = new commonProp();

        proc.commonProp.read();

        proc.peerProp = new peerProp();

        proc.neighbours = proc.peerProp.read(proc.peerId);

        ListIterator<peerProp> itr = proc.neighbours.listIterator();

        Server server = new Server(proc.peerProp.port);
        server.start();

        while(itr.hasNext()) {
            peerProp peer = itr.next();
            if(peer.hasFile) {
                peer.getBitfield().set(0, (int)proc.commonProp.numPieces, true);
            }
            else {
                peer.setBitfield(new BitSet((int) proc.commonProp.numPieces));
            }

            Client client = new Client(peer);
            client.start();

        }






    }
}
