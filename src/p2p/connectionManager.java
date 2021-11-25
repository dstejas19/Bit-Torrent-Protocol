package p2p;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class connectionManager extends Thread{
    int remotePeerId;
    Socket socket;
    ObjectInputStream input;
    ObjectOutputStream output;
    public messageManager msgH = null;

    public connectionManager(Socket socket, ObjectInputStream in, ObjectOutputStream op, int id, messageManager mm) {
        this.socket = socket;
        input = in;
        output = op;
        remotePeerId = id;
        msgH = mm;

        msgH.start();
    }

    public void run() {
        while(true) {
            try {
                String msg = (String) input.readObject();
                System.out.println("Yessssss");

                if(msg == null) {
                    continue;
                }

                msgH.msgQ.add(msg);
                if(msgH.getState().equals(State.WAITING)) {
                    synchronized (msgH) {
                        msgH.notify();
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
