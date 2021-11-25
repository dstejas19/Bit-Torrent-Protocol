package p2p;

import java.io.*;
public class Peer {
    public int peerId;
    public String host;
    public int port;
    public boolean containFile;
    public double downRate = 0;
    public boolean optUnchoked = false;

    public void print() {
        System.out.println("Hello");
    }
}
