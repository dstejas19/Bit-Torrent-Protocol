package p2p;

import p2p.peerProp;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.SimpleFormatter;


public class LogHandler {

    public static Logger LogH(int peerID) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tT]: %5$s %n");
        Logger logger = Logger.getLogger("peer_" + peerID);
        FileHandler fh;

        String path = System.getProperty("user.dir") + File.separator + "log_peer_" + peerID + ".log";
        try {
            fh = new FileHandler(path, true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }
}
