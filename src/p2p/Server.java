package p2p;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
                String msg = (String) input.readObject();
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();

                if(socket != null) {
                    System.out.println(msg);

                    String msgToSend = "From peer" + peerProcess.peerId;
                    output.writeObject(msgToSend);
                    output.flush();

                    new connectionManager(socket, input, output, 1, new messageManager(socket, input, output, 1)).start();//need to extract peer_id from the handshake object
                    System.out.println("hereerer");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
