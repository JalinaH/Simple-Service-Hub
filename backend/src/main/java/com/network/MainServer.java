package com.network;

import com.network.nio.NioFileServer;
import com.network.tcp.ChatServer;
import com.network.udp.UdpHealthServer;

/**
 * Main entry point for the Network Programming Assignment 2
 * NetHub - A multi-service server application
 * 
 * This application demonstrates:
 * 1. TCP Networking with ServerSocket (Connection-oriented, Blocking I/O)
 * 2. Multithreading with ExecutorService (Thread Pool)
 * 3. Thread-safe operations using synchronized blocks
 * 4. UDP Networking with DatagramSocket (Connectionless, Fast)
 * 5. NIO with Selector (Non-blocking I/O, High Scalability)
 * 6. Buffer-oriented I/O with ByteBuffer
 */
public class MainServer {

    public static void main(String[] args) {
        printBanner();
        
        // --- TCP Chat Server (Port 5004) ---
        // Demonstrates: TCP connections, ExecutorService, Runnable, synchronized
        System.out.println("[MainServer] Starting TCP Chat Server...");
        ChatServer tcpChatServer = new ChatServer(5004);
        
        // Create a new thread for the TCP server and start it [cite: 556, 579-580]
        // This allows the server to run independently without blocking the main thread
        new Thread(tcpChatServer, "TCP-Server-Thread").start();
        
        System.out.println("[MainServer] ✓ TCP Chat Server initialized on port 5004");
        System.out.println();

        // --- NIO File Transfer Server (Port 5001) ---
        // Demonstrates: Non-blocking I/O, Selector, ByteBuffer, ServerSocketChannel
        System.out.println("[MainServer] Starting NIO File Transfer Server...");
        NioFileServer nioFileServer = new NioFileServer(5001);
        
        // Create a new thread for the NIO server and start it
        // NIO server uses selector to handle multiple clients in one thread
        new Thread(nioFileServer, "NIO-Server-Thread").start();
        
        System.out.println("[MainServer] ✓ NIO File Server initialized on port 5001");
        System.out.println();

        // --- UDP Health Check Server (Port 5002) ---
        // Demonstrates: UDP datagrams, connectionless protocol, fast health checks
        System.out.println("[MainServer] Starting UDP Health Check Server...");
        UdpHealthServer udpHealthServer = new UdpHealthServer(5002);
        
        // Create a new thread for the UDP server and start it
        // UDP server runs independently and handles packets as they arrive
        new Thread(udpHealthServer, "UDP-Server-Thread").start();
        
        System.out.println("[MainServer] ✓ UDP Health Check Server initialized on port 5002");
        System.out.println();

        printSeparator();
        System.out.println("[MainServer] 🚀 ALL SERVICES ARE RUNNING!");
        System.out.println();
        System.out.println("[MainServer] Service Overview:");
        System.out.println("[MainServer]   Port 5004 - TCP Chat Server (blocking, multithreaded)");
        System.out.println("[MainServer]   Port 5001 - NIO File Server (non-blocking, selector-based)");
        System.out.println("[MainServer]   Port 5002 - UDP Health Server (connectionless, fast)");
        System.out.println();
        System.out.println("[MainServer] Press Ctrl+C to stop all servers");
        printSeparator();
        System.out.println();
    }
    
    private static void printBanner() {
        printSeparator();
        System.out.println("       NetHub - NETWORK SERVER");
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