package com.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implements the TCP Chat Server.
 * Listens for client connections and uses a Thread Pool (ExecutorService)
 * to handle each client concurrently.
 * 
 * Demonstrates multithreading concepts:
 * - ExecutorService for thread pool management
 * - Runnable interface for concurrent task execution
 * - synchronized keyword for thread-safe access to shared resources
 */
public class ChatServer implements Runnable {

    private final int port;
    private final ExecutorService pool; // For Multithreading (Lesson 5) [cite: 774-777]
    private final List<ClientHandler> clients; // Shared resource - requires synchronization

    public ChatServer(int port) {
        this.port = port;
        // Create a fixed-size thread pool with 10 threads [cite: 793, 816]
        // This prevents creating unlimited threads and controls resource usage
        this.pool = Executors.newFixedThreadPool(10);
        // Use ArrayList with synchronized blocks for thread-safe access
        this.clients = new ArrayList<>();
    }

    @Override
    public void run() {
        // Try-with-resources to ensure the ServerSocket is closed automatically [cite: 2175]
        try (ServerSocket serverSocket = new ServerSocket(port)) { // 1. Create ServerSocket [cite: 2109]
            System.out.println("[ChatServer] TCP Server started successfully!");
            System.out.println("[ChatServer] Listening on port " + port);
            System.out.println("[ChatServer] Thread Pool initialized with 10 threads");
            System.out.println("[ChatServer] Waiting for client connections...\n");

            // 2. Infinite loop to continuously accept new connections
            // This loop never blocks other clients because each client is handled
            // in a separate thread from the ExecutorService pool
            while (true) {
                try {
                    // 3. Block and wait for a client to connect [cite: 2117]
                    // This is the ONLY blocking operation in the main server thread
                    Socket clientSocket = serverSocket.accept(); 
                    
                    System.out.println("[ChatServer] ✓ New client connected!");
                    System.out.println("[ChatServer]   Address: " + clientSocket.getInetAddress());
                    System.out.println("[ChatServer]   Port: " + clientSocket.getPort());

                    // 4. Create a new Runnable task (ClientHandler) for the client
                    // The ClientHandler implements Runnable and holds the Socket object
                    ClientHandler clientTask = new ClientHandler(clientSocket, this);
                    
                    // 5. Add client to shared list (thread-safe with synchronized block)
                    synchronized (clients) {
                        clients.add(clientTask);
                        System.out.println("[ChatServer]   Total active clients: " + clients.size());
                    }

                    // 6. Submit the task to the thread pool to be executed [cite: 800, 824]
                    // The ExecutorService will assign this task to an available thread
                    // This is NON-BLOCKING - the main loop continues immediately
                    pool.submit(clientTask);
                    System.out.println("[ChatServer]   Client handler submitted to thread pool\n");
                    
                } catch (IOException e) {
                    System.err.println("[ChatServer] Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[ChatServer] Could not listen on port " + port + ": " + e.getMessage());
        } finally {
            // Cleanup: shutdown the thread pool gracefully
            pool.shutdown();
            System.out.println("[ChatServer] Server shut down");
        }
    }

    /**
     * Broadcasts a message to all connected clients except the sender.
     * Uses synchronized block to prevent race conditions when multiple threads
     * are accessing the shared 'clients' list simultaneously.
     * 
     * @param message The message to broadcast
     * @param sender The ClientHandler that sent the message (excluded from broadcast)
     */
    public void broadcast(String message, ClientHandler sender) {
        // Synchronized block ensures thread-safe iteration over the clients list
        // Multiple ClientHandler threads may call this method concurrently
        synchronized (clients) {
            System.out.println("[ChatServer] Broadcasting to " + (clients.size() - 1) + " clients");
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    /**
     * Removes a client handler from the list when they disconnect.
     * Uses synchronized block to prevent race conditions during list modification.
     * 
     * @param client The ClientHandler to remove
     */
    public void removeClient(ClientHandler client) {
        // Synchronized block ensures thread-safe removal from the clients list
        synchronized (clients) {
            clients.remove(client);
            System.out.println("[ChatServer] Client disconnected: " + client.getClientAddress());
            System.out.println("[ChatServer] Remaining active clients: " + clients.size());
        }
    }
    
    /**
     * Returns the current number of connected clients.
     * Thread-safe method to get client count.
     * 
     * @return Number of active clients
     */
    public int getClientCount() {
        synchronized (clients) {
            return clients.size();
        }
    }
}