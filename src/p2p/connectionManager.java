package p2p;

import messages.bitfieldMessage;

import java.io.EOFException;
import java.io.IOException;
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
        while(!socket.isClosed()) {
            try {
                Object msg = input.readObject();

                msgH.msgQ.add(msg);
                if(msgH.getState().equals(State.WAITING)) {
                    synchronized (msgH) {
                        msgH.notify();
                    }
                }
            } catch (EOFException ex) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Connection manager Exception - ");
                e.printStackTrace();
            }
        }
    }
}
