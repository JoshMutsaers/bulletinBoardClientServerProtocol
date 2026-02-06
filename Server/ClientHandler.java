import java.io.*;
import java.net.*;


public class ClientHandler implements Runnable {
    // Fields
    private Socket socket;
    private Board board;
    private BufferedReader in;
    private PrintWriter out;
    private int boardWidth, boardHeight, noteWidth, noteHeight;
    private String[] validColours;


    // Constructor
    public ClientHandler(Socket socket, Board board, int bWidth, int bHeight, int nWidth, int nHeight, String[] colours) {
        this.socket = socket;
        this.board = board;
        this.boardWidth = bWidth;
        this.boardHeight = bHeight;
        this.noteWidth = nWidth;
        this.noteHeight = nHeight;
        this.validColours = colours;
    }

    // Sets up streams, sends handshake, and processes commands
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            sendHandshake();
            String command;
            while ((command = in.readLine()) != null) {
                String response = handleCommand(command);
                out.println(response);
                if (command.equals("DISCONNECT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Client handler error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    // Sends initial board information to the client
    private void sendHandshake() {
        out.println("BOARD " + boardWidth + " " + boardHeight);
        out.println("NOTE " + noteWidth + " " + noteHeight);
        out.print("COLOURS");
        for (String colour : validColours) {
            out.print(" " + colour);
        }
        out.println();
    }

    // Closes streams and socket connection
    private void cleanup() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
    }

    // Routes commands to the correct board methods
    private String handleCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return "ERROR INVALID_FORMAT Empty command";
        }
        String[] parts = command.trim().split("\\s+");
        String cmd = parts[0];
        try {
            if (cmd.equals("CLEAR")) {
                return board.clear();
            }
            else if (cmd.equals("SHAKE")) {
                return board.shake();
            }
            else if (cmd.equals("DISCONNECT")) {
                return "OK";
            }
            else if (cmd.equals("PIN")) {
                if (parts.length < 3) {
                    return "ERROR INVALID_FORMAT PIN required x y coordinates";
                }
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                return board.pin(x, y);
            }
            else if (cmd.equals("UNPIN")) {
                if (parts.length < 3) {
                    return "ERROR INVALID_FORMAT UNPIN required x y coordinates";
                }
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                return board.unpin(x, y);
            }
            else if (cmd.equals("POST")) {
                if (parts.length < 5) {
                    return "ERROR INVALID_FORMAT POST requires x, y, colour, and message";
                }
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                String colour = parts[3];
                String message = command.substring(command.indexOf(parts[3]) + parts[3].length()).trim();
                return board.post(x, y, colour, message);
            }
            else if (cmd.equals("GET")) {
                if (parts.length >= 2 && parts[1].equals("PINS")) {
                    return board.getPins();
                }
                else {
                    return handleFilteredGet(command);

                }
            }
            else {
                return "ERROR INVALID_FORMAT Unknown command";
            }
        } catch (Exception e) {
            return "ERROR INVALID_FORMAT " + e.getMessage();
        }
    }

    // Parses GET command
    private String handleFilteredGet(String command) {
        String[] parts = command.trim().split("\\s+");
        String colour = null;
        Integer containsX = null;
        Integer containsY = null;
        String refersTo = null;
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (part.startsWith("colour=")) {
                colour = part.substring(7);
            }
            else if (part.startsWith("contains=")) {
                try {
                    containsX = Integer.parseInt(part.substring(9));
                    if (i + 1 < parts.length) {
                        containsY = Integer.parseInt(parts[++i]);
                    }
                } catch (NumberFormatException e) {
                    return "ERROR INVALID_FORMAT Invalid coordinates in contains";
                }
            }
            else if (part.startsWith("refersTo=")) {
                refersTo = part.substring(9);
            }
        } 
        return board.getNotes(colour, containsX, containsY, refersTo);
    }
}