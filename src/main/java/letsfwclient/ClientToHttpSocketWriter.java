package letsfwclient;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientToHttpSocketWriter extends Thread {
    private static final int IF_NOTHING_HAS_BEEN_READ_YET = 10;
    private static final Logger logger = Logger.getLogger(ClientToHttpSocketWriter.class.getName());
    private final DataInputStream clientInputStream;
    private final Socket httpSocket;

    public ClientToHttpSocketWriter(DataInputStream clientInputStream, Socket httpSocket) {
        this.clientInputStream = clientInputStream;
        this.httpSocket = httpSocket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                while (clientInputStream.available() == 0) {
                    Thread.sleep(IF_NOTHING_HAS_BEEN_READ_YET);
                }
                final byte[] bytesFromClient = clientInputStream.readNBytes(clientInputStream.available());
                final OutputStream httpSocketOutputStream = httpSocket.getOutputStream();
                httpSocketOutputStream.write(bytesFromClient);
                logger.info(new String(bytesFromClient));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Something went wrong in the " + currentThread().getName());
        }
    }
}
