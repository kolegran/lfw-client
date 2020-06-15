package letsfwclient;

import java.io.DataInputStream;
import java.net.Socket;

public class LetsForwardClient {

    private static final String LETS_FW_SERVER_HOST = "localhost";
    private static final int LETS_FW_SERVER_PORT = 8081;
    private static final String APP_HOST = "localhost"; // TODO: add ability to use different hosts
    private static final int APP_PORT = 8080;

    public void start(String[] cmdArgs) {
        final int webServerPort = cmdArgs.length == 0 ? APP_PORT : Integer.parseInt(cmdArgs[0]);

        try {
            final Socket clientSocket = new Socket(LETS_FW_SERVER_HOST, LETS_FW_SERVER_PORT);
            final DataInputStream clientInputStream = new DataInputStream(clientSocket.getInputStream());
            System.out.println("Client connected to socket");
            System.out.println(clientInputStream.readUTF());

            final Socket httpSocket = new Socket(APP_HOST, webServerPort);

            final ClientToHttpSocketWriter clientToHttpSocketWriter = new ClientToHttpSocketWriter(clientInputStream, httpSocket);
            clientToHttpSocketWriter.setName("Thread-1:ClientToHttpSocketWriter");
            clientToHttpSocketWriter.start();

            final HttpSocketToClientSocketWriter httpSocketToClientSocketWriter = new HttpSocketToClientSocketWriter(httpSocket, clientSocket);
            httpSocketToClientSocketWriter.setName("Thread-2:HttpSocketToClientSocketWriter");
            httpSocketToClientSocketWriter.start();

            // TODO 2020/16/6: if threads finished -> httpSocket.close()
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
