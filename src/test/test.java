package test;

import p2p.peerProcess;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        RandomAccessFile raf = null;
        long numPieces = 24301474/16384;
        try {
            if(24301474%16384 > 0) {
                numPieces = numPieces + 1;
            }
//
            raf = new RandomAccessFile(System.getProperty("user.dir") + File.separator + "tree.jpg", "r");
            for(long piece=0;piece<numPieces-1;piece++) {
                BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + File.separator + piece + ".part"));
                readWrite(raf, bw, 16384);
                bw.close();
            }

            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir") + File.separator + (numPieces-1) + ".part"));
            if(24301474%16384 > 0) {
                readWrite(raf, bw, 24301474%16384);
            }
            else {
                readWrite(raf, bw, 24301474%16384);
            }
            bw.close();

            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<byte[]> bytesList = new ArrayList<>();

        try {
            for(long i=0;i<numPieces;i++) {
                bytesList.add(Files.readAllBytes(new File(System.getProperty("user.dir") + File.separator + i + ".part").toPath()));
            }

            FileOutputStream fos = new FileOutputStream("final.jpg");

            for (byte[] data: bytesList) {
                fos.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
