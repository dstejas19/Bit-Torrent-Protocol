package p2p;

import messages.bitfieldMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class connectionManager extends Thread{
    int remotePeerId;
    Socket socket;
    ObjectInputStream input;
    ObjectOutputStream output;
    private messageManager msgH = null;

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
                Object msg = input.readObject();

//                if(msg instanceof String) {
//                    String msgString = (String) msg;
//                    System.out.println("Yessssss");
//
//                    if(msgString == null) {
//                        continue;
//                    }
//
//                    msgH.msgQ.add(msgString);
//                    if(msgH.getState().equals(State.WAITING)) {
//                        synchronized (msgH) {
//                            msgH.notify();
//                        }
//                    }
//                }
//
//                byte[] bitfieldMsg = (byte[]) msg;
//
//                System.out.println((int)bitfieldMsg[4]);

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
