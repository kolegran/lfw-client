package letsfwclient;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class LetsFwClient {

    private static final String LETS_FW_SERVER_HOST = "localhost";
    private static final int LETS_FW_SERVER_PORT = 8081;
    private static final int IF_NOTHING_HAS_BEEN_READ_YET = 10;
    private static final int TIME_TO_READ = 1000;
    private static final String APP_HOST = "localhost";
    private static final int APP_PORT = 8080;

    public void start(String[] cmdArgs) {
        int webServerPort = cmdArgs.length == 0 ? APP_PORT : Integer.parseInt(cmdArgs[0]);

        try {
            Socket letsFwClientSocket = new Socket(LETS_FW_SERVER_HOST, LETS_FW_SERVER_PORT);
            DataInputStream letsFwClientInputStream = new DataInputStream(letsFwClientSocket.getInputStream());

            System.out.println("Client connected to socket");
            System.out.println(letsFwClientInputStream.readUTF());

            while (true) {
                while (letsFwClientInputStream.available() == 0) {
                    Thread.sleep(IF_NOTHING_HAS_BEEN_READ_YET);
                }
                Thread.sleep(TIME_TO_READ);

                byte[] bytesFromLetsFwClient = letsFwClientInputStream.readNBytes(letsFwClientInputStream.available());
                System.out.println(new String(bytesFromLetsFwClient));

                Socket httpSocket = new Socket(APP_HOST, webServerPort);
                OutputStream httpSocketOutputStream = httpSocket.getOutputStream();
                httpSocketOutputStream.write(bytesFromLetsFwClient);

                Thread.sleep(TIME_TO_READ);

                InputStream httpSocketInputStream = httpSocket.getInputStream();
                OutputStream letsFwClientSocketOutputStream = letsFwClientSocket.getOutputStream();
                byte[] bytesFromHttpSocket = httpSocketInputStream.readNBytes(httpSocketInputStream.available());
                letsFwClientSocketOutputStream.write(bytesFromHttpSocket);

                httpSocket.close();
            }
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
