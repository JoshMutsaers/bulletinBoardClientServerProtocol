import java.io.*;
import java.net.*;

// ABDULLLLLLLLLLLLL


public class ClientConnection {

    public interface MessageListener { }
    public interface StatusListener { }

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
                        break; // server disconnected
                    }
                    System.out.println("SERVER: " + line);
                }
            } catch (IOException e) {
                if (running) {
                    System.out.println("Read error: " + e.getMessage());
                    running = false;
                }
            }
        });
    
        readerThread.start();
    }
    public ClientConnection(MessageListener msgListener, StatusListener statusListener) {

    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void connect(String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        running = true;
        startReaderThread(); // Start the reader thread

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

        try {
            if (reader != null) { reader.close(); reader = null; }
            if (writer != null) { writer.close(); writer = null; }
            if (socket != null && !socket.isClosed()) { socket.close(); socket = null; }
        } catch (IOException ignored) { }
    }
}
