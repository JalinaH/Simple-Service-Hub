package com.network;

import com.network.tcp.ChatServer;
import com.network.udp.UdpHealthServer;

/**
 * Main entry point for the Network Programming Assignment 2
 * Simple Service Hub - A multi-service server application
 * 
 * This application demonstrates:
 * 1. TCP Networking with ServerSocket (Connection-oriented)
 * 2. Multithreading with ExecutorService (Thread Pool)
 * 3. Thread-safe operations using synchronized blocks
 * 4. UDP Networking with DatagramSocket (Connectionless)
 * 5. Concurrent client handling without blocking
 */
public class MainServer {

    public static void main(String[] args) {
        printBanner();
        
        // --- TCP Chat Server (Port 5000) ---
        // Demonstrates: TCP connections, ExecutorService, Runnable, synchronized
        System.out.println("[MainServer] Starting TCP Chat Server...");
        ChatServer tcpChatServer = new ChatServer(5000);
        
        // Create a new thread for the TCP server and start it [cite: 556, 579-580]
        // This allows the server to run independently without blocking the main thread
        new Thread(tcpChatServer, "TCP-Server-Thread").start();
        
        System.out.println("[MainServer] TCP Chat Server initialized on port 5000");
        System.out.println();

        // --- UDP Health Check Server (Port 5002) ---
        // Demonstrates: UDP datagrams, connectionless protocol, fast health checks
        System.out.println("[MainServer] Starting UDP Health Check Server...");
        UdpHealthServer udpHealthServer = new UdpHealthServer(5002);
        
        // Create a new thread for the UDP server and start it
        // UDP server runs independently and handles packets as they arrive
        new Thread(udpHealthServer, "UDP-Server-Thread").start();
        
        System.out.println("[MainServer] UDP Health Check Server initialized on port 5002");
        System.out.println();
        
        // --- NIO File Transfer Server (Port 5001) - To be added later ---
        // new Thread(new NioFileServer(5001)).start();

        printSeparator();
        System.out.println("[MainServer] All services are running!");
        System.out.println("[MainServer] TCP Chat: port 5000 (connection-oriented)");
        System.out.println("[MainServer] UDP Health: port 5002 (connectionless)");
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