package p2p;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Thread{
    public peerProp remotePeer;
    messageManager msgH = null;


    public Client(peerProp peer) {
        remotePeer = peer;
    }

    public void run() {
        try {
//            System.out.println("Sending message to " + remotePeer.peerId);
            Socket clientSocket = new Socket(remotePeer.host, remotePeer.port);
//            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());;
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();

            String msg = "Hello From Peer" + peerProcess.peerId + "To peer " + remotePeer.peerId;

            output.writeObject(msg);
            output.flush();

            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());;
            String msgReceived = (String) input.readObject();
            System.out.println(msgReceived);

            output.writeObject("Lets start - from client " + peerProcess.peerId);
            output.flush();

            new connectionManager(clientSocket, input, output, remotePeer.peerId, new messageManager(clientSocket, input, output, remotePeer.peerId)).start();//need to extract peer_id from the handshake object
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
