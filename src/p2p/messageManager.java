package p2p;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class messageManager extends Thread{
    Socket socket;
    ObjectInputStream input;
    ObjectOutputStream output;
    int peerId;
    public ConcurrentLinkedQueue<String> msgQ = new ConcurrentLinkedQueue<String>();

    public messageManager(Socket socket, ObjectInputStream in, ObjectOutputStream op, int id) {
        this.socket = socket;
        input = in;
        output = op;
        peerId = id;
    }

    public void run() {
        int count = 0;
        while(count < 5) {
            if(!msgQ.isEmpty()) {
                try {
                    String msg = msgQ.poll();

                    System.out.println(msg);
                    output.writeObject("To peer " + peerId + ", from peer " + peerProcess.peerId);
                    count++;
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

}
