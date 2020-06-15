package letsfwclient;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientToHttpSocketWriter extends Thread {
    private static final int IF_NOTHING_HAS_BEEN_READ_YET = 10;
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
                // read from client socket and write to httpSocket
                final byte[] bytesFromClient = clientInputStream.readNBytes(clientInputStream.available());
                final OutputStream httpSocketOutputStream = httpSocket.getOutputStream();
                httpSocketOutputStream.write(bytesFromClient);
                System.out.println(new String(bytesFromClient));
            }
        } catch (Exception e) {
            System.out.println("Something went wrong in the " + currentThread().getName());
        }
    }
}
