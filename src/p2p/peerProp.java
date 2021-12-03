package p2p;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Objects;


public class peerProp {
    int peerId;
    String host;
    int port;
    boolean hasFile;
    public BitSet bitfield;
//    public double downloadRate;
//    public double start;
    public boolean sendFile = false;
    public boolean optimisticallySendFile = false;
    public boolean receiveFile = false;
    public int piecesSent = 0;

    public int getPeerId() {
        return peerId;
    }

    public int getPiecesSent() {
        return piecesSent;
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

    public void setBitfield(BitSet bt) {
        bitfield = bt;
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
                    hasFile = Objects.equals(words[3], "1");
                }
                else {
                    peerProp peer = new peerProp();

                    peer.peerId = Integer.parseInt(words[0]);
                    peer.host = words[1];
                    peer.port = Integer.parseInt(words[2]);

                    peer.hasFile = Objects.equals(words[3], "1");

                    peers.add(peer);

                }

                line = reader.readLine();
            }
        } catch (Exception e) {
            System.out.println("PeerProp Exception - " + e);
        }

        return peers;
    }
}
