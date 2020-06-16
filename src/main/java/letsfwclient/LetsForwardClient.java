package letsfwclient;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LetsForwardClient {
    private static final Logger logger = Logger.getLogger(LetsForwardClient.class.getName());
    private static final String LETS_FW_SERVER_HOST = "localhost";
    private static final int LETS_FW_SERVER_PORT = 8081;
    private static final String APP_HOST = "localhost"; // TODO: add ability to use different hosts
    private static final int APP_PORT = 8080;

    public void start(String[] cmdArgs) {
        final int webServerPort = cmdArgs.length == 0 ? APP_PORT : Integer.parseInt(cmdArgs[0]);

        try {
            final Socket clientSocket = new Socket(LETS_FW_SERVER_HOST, LETS_FW_SERVER_PORT);
            final DataInputStream clientInputStream = new DataInputStream(clientSocket.getInputStream());
            logger.log(Level.INFO, "Client connected to socket ");
            logger.log(Level.INFO, "The port is a " + clientInputStream.readUTF());

            final Socket httpSocket = new Socket(APP_HOST, webServerPort);

            final ClientToHttpSocketWriter clientToHttpSocketWriter = new ClientToHttpSocketWriter(clientInputStream, httpSocket);
            clientToHttpSocketWriter.setName("ClientToHttpSocketWriter thread");
            clientToHttpSocketWriter.start();

            final HttpSocketToClientSocketWriter httpSocketToClientSocketWriter = new HttpSocketToClientSocketWriter(httpSocket, clientSocket);
            httpSocketToClientSocketWriter.setName("HttpSocketToClientSocketWriter thread");
            httpSocketToClientSocketWriter.start();

            // TODO 2020/16/6: if threads finished -> httpSocket.close()
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }
}
