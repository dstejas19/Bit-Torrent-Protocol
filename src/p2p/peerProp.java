package p2p;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.BitSet;


public class peerProp {
    int peerId;
    String host;
    int port;
    boolean hasFile;
    static BitSet bitfield;

    public int getPeerId() {
        return peerId;
    }

    public boolean isHasFile() {
        return hasFile;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public BitSet getBitfield() {
        return bitfield;
    }

    public void setBitfield(BitSet bitfield) {
        this.bitfield = bitfield;
    }

    public void setHasFile(boolean hasFile) {
        this.hasFile = hasFile;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }

    public void setPort(int port) {
        this.port = port;
    }

    //    peerProp(int id) {
//        peerId = id;
//    }

    public ArrayList<peerProp> read(int id) {
        ArrayList<peerProp> peers = new ArrayList<>();
        File f = new File("PeerInfo.cfg");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line = reader.readLine();

            while(line != null) {
                String words[] = line.split("\\s");

                if(Integer.parseInt(words[0]) == id) {
                    peerId = Integer.parseInt(words[0]);
                    host = words[1];
                    port = Integer.parseInt(words[2]);
                    hasFile = Boolean.parseBoolean(words[3]);
                }
                else {
                    peerProp peer = new peerProp();

                    peer.peerId = Integer.parseInt(words[0]);
                    peer.host = words[1];
                    peer.port = Integer.parseInt(words[2]);
                    peer.hasFile = Boolean.parseBoolean(words[3]);

                    peers.add(peer);

                }

                line = reader.readLine();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return peers;
    }
}
