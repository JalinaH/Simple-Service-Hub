package com.network;

import com.network.tcp.ChatServer;

public class MainServer {

    public static void main(String[] args) {
        // --- Your Part (TCP + Multithreading) ---
        // Create an instance of your ChatServer
        ChatServer tcpChatServer = new ChatServer(5000);
        // Create a new thread for the server and start it [cite: 556, 579-580]
        new Thread(tcpChatServer).start();

        // --- Other Members' Parts (will be added later) ---
        // new Thread(new UdpHealthServer(5002)).start();
        // new Thread(new NioFileServer(5001)).start();

        System.out.println("[MainServer] All services are starting up...");
    }
}