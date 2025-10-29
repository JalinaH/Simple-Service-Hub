package com.network.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A Runnable task that handles all communication with a single client [cite: 587-591].
 * An instance of this class is executed by a thread in the ExecutorService.
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientAddress;

    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.clientAddress = socket.getInetAddress().toString();
    }

    @Override
    public void run() {
        try {
            // Setup streams to send (PrintWriter) and receive (BufferedReader) data [cite: 2024-2025, 2069]
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            // Loop and read data from the client
            while ((inputLine = in.readLine()) != null) { 
                System.out.println("[ClientHandler " + clientAddress + "] Received: " + inputLine);
                
                // Broadcast the message to all other clients via the server
                server.broadcast(clientAddress + ": " + inputLine, this);
            }

        } catch (IOException e) {
            // This often happens when a client disconnects abruptly
            System.out.println("[ClientHandler " + clientAddress + "] Connection lost: " + e.getMessage());
        } finally {
            // Cleanup
            server.removeClient(this);
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                clientSocket.close(); // Close the socket [cite: 2131]
            } catch (IOException e) {
                System.err.println("[ClientHandler] Error closing socket: " + e.getMessage());
            }
        }
    }

    /**
     * Sends a message to this specific client.
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    public String getClientAddress() {
        return clientAddress;
    }
}