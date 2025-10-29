package com.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implements the TCP Chat Server.
 * Listens for client connections and uses a Thread Pool (ExecutorService)
 * to handle each client concurrently.
 */
public class ChatServer implements Runnable {

    private final int port;
    private final ExecutorService pool; // For Multithreading (Lesson 5) [cite: 774-777]
    private final List<ClientHandler> clients; // Thread-safe list to store all client handlers

    public ChatServer(int port) {
        this.port = port;
        // Create a fixed-size thread pool [cite: 793, 816]
        this.pool = Executors.newFixedThreadPool(10);
        // Use a thread-safe list for managing clients
        this.clients = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        // Try-with-resources to ensure the ServerSocket is closed automatically [cite: 2175]
        try (ServerSocket serverSocket = new ServerSocket(port)) { // 1. Create ServerSocket [cite: 2109]
            System.out.println("[ChatServer] Listening on port " + port);

            // 2. Loop forever to accept new connections
            while (true) {
                try {
                    // 3. Block and wait for a client to connect [cite: 2117]
                    Socket clientSocket = serverSocket.accept(); 
                    System.out.println("[ChatServer] New client connected: " + clientSocket.getInetAddress());

                    // 4. Create a new task (Runnable) for the client
                    ClientHandler clientTask = new ClientHandler(clientSocket, this);
                    clients.add(clientTask);

                    // 5. Submit the task to the thread pool to be executed [cite: 800, 824]
                    pool.submit(clientTask);
                    
                } catch (IOException e) {
                    System.err.println("[ChatServer] Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[ChatServer] Could not listen on port " + port + ": " + e.getMessage());
        }
    }

    /**
     * Broadcasts a message to all connected clients except the sender.
     * This method must be thread-safe.
     */
    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * Removes a client handler from the list when they disconnect.
     * This method must be thread-safe.
     */
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("[ChatServer] Client disconnected: " + client.getClientAddress());
    }
}