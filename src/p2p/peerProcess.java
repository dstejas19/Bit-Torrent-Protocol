package p2p;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class peerProcess {
    public static commonProp commonProperty;
    public static peerProp peerProperty;
    public static int peerId;
    public ArrayList<peerProp> allPeers;
    public static ConcurrentHashMap<Integer, peerProp> peerMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, peerProp> interestedPeers = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, connectionManager> connectionMap = new ConcurrentHashMap<>();
    public static Logger log;

    public static void main(String[] args) {
        peerProcess proc = new peerProcess();
        peerId = Integer.parseInt(args[0]);
        System.out.println(peerId);

        commonProperty = new commonProp();

        commonProperty.read();

        peerProperty = new peerProp();

        proc.allPeers = peerProperty.read(peerId);
        peerProcess.log=LogHandler.LogH(peerId);

        if(peerProcess.peerProperty.hasFile) {
            peerProcess.peerProperty.setBitfield(new BitSet((int) commonProperty.numPieces));
            peerProcess.peerProperty.getBitfield().set(0, (int) commonProperty.numPieces, true);
            setupDirectory(true);
        }
        else {
            peerProcess.peerProperty.setBitfield(new BitSet((int) commonProperty.numPieces));
            setupDirectory(false);
        }


//        System.out.println(peerProcess.peerProperty.getBitfield());

        Server server = new Server(peerProperty.port);
        server.start();


//        System.out.println(peerProcess.peerProperty.getBitfield());

        for (peerProp peer : proc.allPeers) {
            //            System.out.println(peer.peerId);
//            System.out.println(peerProcess.peerProperty.getBitfield());
            if (peer.hasFile) {
                peer.setBitfield(new BitSet((int) commonProperty.numPieces));
                peer.getBitfield().set(0, (int) commonProperty.numPieces, true);
            } else {
                peer.setBitfield(new BitSet((int) commonProperty.numPieces));
            }

            peerMap.put(peer.peerId, peer);

            Client client = new Client(peer);
            client.start();

        }

        new chokeUnchoke().start();
        new optimisticallyUnchoke().start();
        new End().start();


    }

    public static void setupDirectory(boolean hasFile) {
        String pwd = System.getProperty("user.dir");

        peerProcess.commonProperty.fileDir = pwd + File.separator + "peer_" + peerProcess.peerId;

        if(!hasFile) {
            File f = new File(peerProcess.commonProperty.fileDir);
            if(!f.exists()) {
                f.mkdirs();
            }
        }
        else {
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(peerProcess.commonProperty.fileDir + File.separator + peerProcess.commonProperty.fileName, "r");
                for(long piece=0;piece<peerProcess.commonProperty.numPieces-1;piece++) {
                    BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(peerProcess.commonProperty.fileDir + File.separator + piece + ".part"));
                    readWrite(raf, bw, peerProcess.commonProperty.pieceSize);
                    bw.close();
                }

                BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(peerProcess.commonProperty.fileDir + File.separator + (peerProcess.commonProperty.numPieces-1) + ".part"));
                if(peerProcess.commonProperty.remSize > 0) {
                    readWrite(raf, bw, (peerProcess.commonProperty.remSize));
                }
                else {
                    readWrite(raf, bw, (peerProcess.commonProperty.pieceSize));
                }
                bw.close();

                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if(val != -1) {
            bw.write(buf);
        }
    }
}
