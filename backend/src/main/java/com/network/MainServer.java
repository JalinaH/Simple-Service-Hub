package com.network;

import com.network.tcp.ChatServer;

/**
 * Main entry point for the Network Programming Assignment 2
 * Simple Service Hub - A multi-service server application
 * 
 * This application demonstrates:
 * 1. TCP Networking with ServerSocket
 * 2. Multithreading with ExecutorService (Thread Pool)
 * 3. Thread-safe operations using synchronized blocks
 * 4. Concurrent client handling without blocking
 */
public class MainServer {

    public static void main(String[] args) {
        printBanner();
        
        // --- TCP Chat Server (Port 5000) ---
        // Demonstrates: TCP connections, ExecutorService, Runnable, synchronized
        System.out.println("[MainServer] Starting TCP Chat Server...");
        ChatServer tcpChatServer = new ChatServer(5000);
        
        // Create a new thread for the server and start it [cite: 556, 579-580]
        // This allows the server to run independently without blocking the main thread
        new Thread(tcpChatServer, "TCP-Server-Thread").start();
        
        System.out.println("[MainServer] TCP Chat Server initialized");
        System.out.println();

        // --- Other Members' Parts (will be added later) ---
        // UDP Health Check Server (Port 5002)
        // new Thread(new UdpHealthServer(5002)).start();
        
        // NIO File Transfer Server (Port 5001)
        // new Thread(new NioFileServer(5001)).start();

        printSeparator();
        System.out.println("[MainServer] All services are starting up...");
        System.out.println("[MainServer] Press Ctrl+C to stop the server");
        printSeparator();
        System.out.println();
    }
    
    private static void printBanner() {
        printSeparator();
        System.out.println("       SIMPLE SERVICE HUB - NETWORK SERVER");
        printSeparator();
        System.out.println();
    }
    
    private static void printSeparator() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 60; i++) {
            sb.append("=");
        }
        System.out.println(sb.toString());
    }
}