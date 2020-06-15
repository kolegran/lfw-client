package letsfwclient;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpSocketToClientSocketWriter extends Thread {
    private static final Logger logger = Logger.getLogger(HttpSocketToClientSocketWriter.class.getName());
    private final Socket httpSocket;
    private final Socket clientSocket;

    public HttpSocketToClientSocketWriter(Socket httpSocket, Socket clientSocket) {
        this.httpSocket = httpSocket;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // read from http socket and write to client socket
                final InputStream httpSocketInputStream = httpSocket.getInputStream();
                final OutputStream clientSocketOutputStream = clientSocket.getOutputStream();
                byte[] bytesFromHttpSocket = httpSocketInputStream.readNBytes(httpSocketInputStream.available());
                clientSocketOutputStream.write(bytesFromHttpSocket);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Something went wrong in the " + currentThread().getName());
        }
    }
}
