import java.io.*;
import java.net.*;

public class BBoard {
    public static void main(String[] args) {
        // Check if there are enough arguments
        if (args.length < 6) {
            System.err.println("Use: java BBoard <port> <board_width> <board_height> <colour1> ... <colourN>");
            return;
        }

        // Parse integers
        int port, boardWidth, boardHeight, noteWidth, noteHeight;
        try {
            port = Integer.parseInt(args[0]);
            boardWidth = Integer.parseInt(args[1]);
            boardHeight = Integer.parseInt(args[2]);
            noteWidth = Integer.parseInt(args[3]);
            noteHeight = Integer.parseInt(args[4]);
        } catch (Exception e) {
            System.err.println("ERROR: Invalid integer in arguments");
            return;
        }

        // Extract colours
        String[] colours = new String[args.length - 5];
        for (int i = 5; i < args.length; i++) {
            colours[i - 5] = args[i];
        }

        // Create the board
        Board board = new Board(boardWidth, boardHeight, noteWidth, noteHeight, colours);
        System.out.println("Board created: " + boardWidth + "X" + boardHeight);

        // Create ServerSocket and listen for clients
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                // Create handler and start in new thread
                ClientHandler handler = new ClientHandler(clientSocket, board, boardWidth, boardHeight, noteWidth, noteHeight, colours);
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}