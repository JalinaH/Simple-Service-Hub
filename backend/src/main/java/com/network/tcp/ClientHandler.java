package com.network.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A Runnable task that handles all communication with a single client [cite: 587-591].
 * 
 * KEY CONCEPT: This class implements Runnable, making it executable by a thread.
 * Each instance of ClientHandler:
 * - Holds a Socket object (the client connection)
 * - Runs in its own thread from the ExecutorService thread pool
 * - Handles I/O operations without blocking other clients
 * - Uses synchronized methods/blocks when accessing shared resources
 * 
 * An instance of this class is executed by a thread in the ExecutorService pool.
 * This allows the server to handle multiple clients concurrently without blocking.
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;  // The client's socket connection
    private final ChatServer server;     // Reference to the main server for broadcasting
    private PrintWriter out;             // Output stream to send data to client
    private BufferedReader in;           // Input stream to receive data from client
    private final String clientAddress;  // Client's address for identification

    /**
     * Constructor: Holds the Socket object passed from the main server loop
     * 
     * @param socket The client's Socket connection from serverSocket.accept()
     * @param server Reference to the ChatServer for callback operations
     */
    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.clientAddress = socket.getInetAddress().toString() + ":" + socket.getPort();
    }

    /**
     * The run() method is called when this Runnable is executed by a thread.
     * This is where the actual client handling logic runs in a separate thread.
     * 
     * IMPORTANT: This method runs concurrently with other ClientHandler instances.
     * Multiple threads execute this code simultaneously for different clients.
     */
    @Override
    public void run() {
        System.out.println("[ClientHandler " + clientAddress + "] Thread started - handling client");
        System.out.println("[ClientHandler " + clientAddress + "] Running in thread: " + Thread.currentThread().getName());
        
        try {
            // Setup streams to send (PrintWriter) and receive (BufferedReader) data [cite: 2024-2025, 2069]
            out = new PrintWriter(clientSocket.getOutputStream(), true);  // true = auto-flush
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Send welcome message to the client
            out.println("Welcome to the TCP Chat Server!");
            out.println("You are connected as: " + clientAddress);
            out.println("Start typing to chat...\n");

            String inputLine;
            // Loop and read data from the client
            // This blocks until data is received, but doesn't block other clients
            // because each client has its own thread
            while ((inputLine = in.readLine()) != null) { 
                System.out.println("[ClientHandler " + clientAddress + "] Received: " + inputLine);
                
                // Handle special commands
                if (inputLine.equalsIgnoreCase("/quit") || inputLine.equalsIgnoreCase("/exit")) {
                    out.println("Goodbye!");
                    break;
                }
                
                if (inputLine.equalsIgnoreCase("/clients")) {
                    out.println("Active clients: " + server.getClientCount());
                    continue;
                }
                
                // Broadcast the message to all other clients via the server
                // This method uses synchronized blocks for thread safety
                server.broadcast(clientAddress + ": " + inputLine, this);
            }

        } catch (IOException e) {
            // This often happens when a client disconnects abruptly
            System.out.println("[ClientHandler " + clientAddress + "] Connection lost: " + e.getMessage());
        } finally {
            // Cleanup: Always executed regardless of how the try block exits
            // Remove this client from the server's list (thread-safe operation)
            server.removeClient(this);
            
            // Close all resources
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                clientSocket.close(); // Close the socket [cite: 2131]
                System.out.println("[ClientHandler " + clientAddress + "] Resources cleaned up, thread terminating");
            } catch (IOException e) {
                System.err.println("[ClientHandler " + clientAddress + "] Error closing socket: " + e.getMessage());
            }
        }
    }

    /**
     * Sends a message to this specific client.
     * This method is called by the ChatServer.broadcast() method from different threads.
     * 
     * The PrintWriter is thread-safe for individual write operations,
     * so we don't need explicit synchronization here.
     * 
     * @param message The message to send to the client
     */
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    /**
     * Returns the client's address identifier.
     * 
     * @return The client's address as a String
     */
    public String getClientAddress() {
        return clientAddress;
    }
}