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
    private boolean running;
    private Thread readerThread;

    private void startReaderThread() {
        readerThread = new Thread(() -> {
            try {
                while (running) {
                    String line = reader.readLine();
                    if (line == null) {
                        running = false;
                        if (statusListener != null) statusListener.onStatus("disconnected");
                        break;
                    }
                    if (msgListener != null) msgListener.onMessage(line);
                }
            } catch (IOException e) {
                if (running) {
                    running = false;
                    if (statusListener != null) statusListener.onStatus("error: " + e.getMessage());
                }
            }
        });
    
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
        this.socket = new Socket(ip, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        running = true;
        if (statusListener != null) statusListener.onStatus("connected");
        startReaderThread();
    }

    public void sendLine(String line) {
        if (!isConnected()) {
            System.out.println("Not connected.");
            return;
        }
        writer.println(line);
    }

    public void disconnect() {
        running = false;
        if (statusListener != null) statusListener.onStatus("disconnected");
        try {
            if (reader != null) { reader.close(); reader = null; }
            if (writer != null) { writer.close(); writer = null; }
            if (socket != null && !socket.isClosed()) { socket.close(); socket = null; }
        } catch (IOException ignored) { }
    }
}
