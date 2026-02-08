import java.io.*;
import java.net.*;

public class ClientConnection {

    public interface MessageListener {
        void onMessage(String line);
    }

    public interface StatusListener {
        void onStatus(String status);
    }

    private final MessageListener msgListener;
    private final StatusListener statusListener;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    // FIX: volatile
    private volatile boolean running;

    private Thread readerThread;

    private void startReaderThread() {
        readerThread = new Thread(() -> {
            try {
                while (running) {
                    String line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    if (msgListener != null) {
                        msgListener.onMessage(line);
                    }
                }
            } catch (IOException e) {
                if (running && statusListener != null) {
                    statusListener.onStatus("error: " + e.getMessage());
                }
            } finally {
                running = false;
                if (statusListener != null) {
                    statusListener.onStatus("disconnected");
                }
            }
        });

        readerThread.setDaemon(true);
        readerThread.start();
    }

    public ClientConnection(MessageListener msgListener, StatusListener statusListener) {
        this.msgListener = msgListener;
        this.statusListener = statusListener;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);

        reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        writer = new PrintWriter(
                socket.getOutputStream(), true);

        running = true;

        if (statusListener != null) {
            statusListener.onStatus("connected");
        }

        startReaderThread();
    }

    public void sendLine(String line) {
        if (!isConnected()) {
            System.out.println("Not connected.");
            return;
        }
        writer.println(line);
    }

    // FIX: unblock readLine + safe shutdown
    public void disconnect() {

        running = false;

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}

        try {
            if (reader != null) {
                reader.close();
                reader = null;
            }
        } catch (IOException ignored) {}

        if (writer != null) {
            writer.close();
            writer = null;
        }

        socket = null;

        if (statusListener != null) {
            statusListener.onStatus("disconnected");
        }
    }
}
