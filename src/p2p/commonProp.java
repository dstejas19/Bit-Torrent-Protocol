package p2p;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

public class commonProp {
    public int numNeighbours;
    public int unchokingInterval;
    public int optunchokingInterval;
    public String fileName;
    public long fileSize;
    public long pieceSize;
    public long numPieces;

    public void read() {
        System.out.println("Path is " + System.getProperty("user.dir"));
        File f = new File("common.cfg");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line = reader.readLine();

            while(line != null) {
                String words[] = line.split("\\s");

                if(words[0].toLowerCase().equals("numberofpreferredneighbors")) {
                    numNeighbours = Integer.parseInt(words[1]);
                }
                else if(words[0].toLowerCase().equals("unchokinginterval")) {
                    unchokingInterval = Integer.parseInt(words[1]);
                }
                else if(words[0].toLowerCase().equals("optimisticunchokinginterval")) {
                    optunchokingInterval = Integer.parseInt(words[1]);
                }
                else if(words[0].toLowerCase().equals("filename")) {
                    fileName = words[1];
                }
                else if(words[0].toLowerCase().equals("filesize")) {
                    fileSize = Long.parseLong(words[1]);
                }
                else if(words[0].toLowerCase().equals("piecesize")) {
                    pieceSize = Long.parseLong(words[1]);
                }

                line = reader.readLine();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        numPieces = fileSize/pieceSize;

        if(fileSize%pieceSize > 0) {
            numPieces = numPieces + 1;
        }
    }
}