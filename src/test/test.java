package test;

import messages.pieceMessage;
import p2p.peerProcess;
import p2p.peerProp;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class test {
    public static void main(String[] args) throws InterruptedException {
//        RandomAccessFile raf = null;
//        long numPieces = 24301474/16384;
//        try {
//            if(24301474%16384 > 0) {
//                numPieces = numPieces + 1;
//            }
////
//            raf = new RandomAccessFile(System.getProperty("user.dir") + File.separator + "tree.jpg", "r");
//            for(long piece=0;piece<numPieces-1;piece++) {
//                BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + File.separator + piece + ".part"));
//                readWrite(raf, bw, 16384);
//                bw.close();
//            }
//
//            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + File.separator + (numPieces-1) + ".part"));
//            if(24301474%16384 > 0) {
//                readWrite(raf, bw, 24301474%16384);
//            }
//            else {
//                readWrite(raf, bw, 24301474%16384);
//            }
//            bw.close();
//
//            raf.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        List<byte[]> bytesList = new ArrayList<>();
//
//        try {
//            for(long i=0;i<numPieces;i++) {
//                bytesList.add(Files.readAllBytes(new File(System.getProperty("user.dir") + File.separator + i + ".part").toPath()));
//            }
//
//            FileOutputStream fos = new FileOutputStream("final.jpg");
//
//            for (byte[] data: bytesList) {
//                fos.write(data);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("main");
//        new Thread() {
//            public void run(){
//                System.out.println("Starting");
//
//                while(true) {
//                    System.out.println("hey");
//                    try {
//                        Thread.sleep(10000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
//
//        for(int i=0;i<1000;i++) {
//            System.out.println(i);
//            Thread.sleep(1000);
////        }
//        peerProp p1 = new peerProp();
//        peerProp p2 = new peerProp();
//        peerProp p3 = new peerProp();
//        peerProp p4 = new peerProp();
//        peerProp p5 = new peerProp();
//
//        p1.piecesSent = 10;
//        p2.piecesSent = 20;
//        p3.piecesSent = 30;
//        p4.piecesSent = 40;
//        p5.piecesSent = 50;
//
//        ArrayList<peerProp> interested = new ArrayList<>(peerProcess.interestedPeers.values());
//        interested.add(p1);
//        interested.add(p2);
//        interested.add(p3);
//        interested.add(p4);
//        interested.add(p5);
//
//        Collections.sort(interested, Comparator.comparingInt(peerProp::getPiecesSent).reversed());
//
//        for(int i=0;i<5;i++) {
//            System.out.println(interested.get(i).piecesSent);
//        }
//
//        byte[] piecefile = new byte[0];
//        try {
//            piecefile = Files.readAllBytes(new File(System.getProperty("user.dir") + File.separator + "peer_1001" + File.separator + 5 + ".part").toPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        pieceMessage pm = new pieceMessage(piecefile);
    }

//    public static void createFile() throws IOException {
//
//    }

    static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if(val != -1) {
            bw.write(buf);
        }
    }
}
