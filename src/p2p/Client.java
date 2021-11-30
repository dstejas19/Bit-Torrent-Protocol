package p2p;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import messages.bitfieldMessage;
import messages.handshakeMessage;

public class Client extends Thread{
    public peerProp remotePeer;


    public Client(peerProp peer) {
        this.remotePeer = peer;
    }

    public void run() {
        try {
//            System.out.println("Sending message to " + remotePeer.peerId);
//            System.out.println("Client " + remotePeer.peerId + " " + peerProcess.peerProperty.getBitfield());
            Socket clientSocket = new Socket(remotePeer.host, remotePeer.port);
//            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());;
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();

            handshakeMessage hsm = new handshakeMessage();

            output.writeObject(hsm.message);
            output.flush();

            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            byte[] msg = (byte[]) input.readObject();

            int peerId = Common.verify(msg);

            if(peerId == -1) {
                return;
            }

            System.out.println("Peer " + peerProcess.peerId + " received message from " + peerId);

            bitfieldMessage bm = new bitfieldMessage();
            output.writeObject(bm.message);
            output.flush();

            new connectionManager(clientSocket, input, output, remotePeer.peerId, new messageManager(clientSocket, input, output, remotePeer.peerId)).start();//need to extract peer_id from the handshake object
        } catch (ConnectException e) {
            System.out.println(remotePeer.peerId + " is not available");
        }
        catch (Exception e) {
            System.out.println("Client exception - " + e);
        }
    }
}
