package p2p;

import messages.handshakeMessage;
import messages.bitfieldMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class Server extends Thread {
    int port;

    public Server(int serverPort) {
        port = serverPort;
    }

    public void run() {
        try {
            ServerSocket listener = new ServerSocket(port);
            System.out.println("Server started at " + port);

            while(true) {
                Socket socket = listener.accept();

                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                byte[] msg = (byte[]) input.readObject();

                int peerId = Common.verify(msg);

                if(peerId == -1) {
                    break;
                }

                System.out.println("Peer " + peerProcess.peerId + " received message from " + peerId);

                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();

                handshakeMessage hsm = new handshakeMessage();

                output.writeObject(hsm.message);
                output.flush();

                //send bitfield
                bitfieldMessage bm = new bitfieldMessage();
                output.writeObject(bm.message);
                output.flush();

                new connectionManager(socket, input, output, peerId, new messageManager(socket, input, output, peerId)).start();//need to extract peer_id from the handshake object
                System.out.println("hereerer");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
