package com.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UDP Health Check Server - Demonstrates connectionless communication
 * 
 * UDP (User Datagram Protocol) is a connectionless, fast protocol ideal for:
 * - Health checks
 * - Status monitoring
 * - Non-critical messages where occasional packet loss is acceptable
 * 
 * Key Differences from TCP:
 * - No connection establishment (no accept/connect handshake)
 * - No guaranteed delivery
 * - No ordering guarantee
 * - Faster and more lightweight
 * - Each message is independent (stateless)
 * 
 * This server listens on Port 5002 and responds to health check requests.
 */
public class UdpHealthServer implements Runnable {

    private final int port;
    private DatagramSocket socket;  // UDP socket (no ServerSocket needed!)
    private volatile boolean running;
    private static final int BUFFER_SIZE = 1024;  // Max size for incoming packets

    /**
     * Constructor
     * @param port The port to bind the UDP server to
     */
    public UdpHealthServer(int port) {
        this.port = port;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            // 1. Create and bind DatagramSocket to the specified port
            // Unlike TCP, we don't need ServerSocket - DatagramSocket handles everything
            socket = new DatagramSocket(port);
            
            System.out.println("[UdpHealthServer] UDP Health Check Server started successfully!");
            System.out.println("[UdpHealthServer] Listening on port " + port);
            System.out.println("[UdpHealthServer] Protocol: UDP (User Datagram Protocol)");
            System.out.println("[UdpHealthServer] Service: Health Check / Status Monitor");
            System.out.println("[UdpHealthServer] Waiting for health check requests...\n");

            // 2. Infinite loop to continuously receive and process UDP packets
            // Unlike TCP's accept() which creates new sockets, UDP reuses the same socket
            while (running) {
                try {
                    // 3. Prepare a buffer to receive incoming data
                    byte[] receiveBuffer = new byte[BUFFER_SIZE];
                    
                    // 4. Create a DatagramPacket to receive data
                    // This packet will hold:
                    // - The received data (in receiveBuffer)
                    // - The sender's IP address (automatically filled by receive())
                    // - The sender's port (automatically filled by receive())
                    DatagramPacket receivePacket = new DatagramPacket(
                        receiveBuffer, 
                        receiveBuffer.length
                    );

                    // 5. Block and wait for incoming packet (similar to accept() in TCP)
                    // This is the ONLY blocking operation in UDP server
                    // Once packet arrives, it continues immediately
                    socket.receive(receivePacket);

                    // 6. Extract information from the received packet
                    InetAddress clientAddress = receivePacket.getAddress();  // Sender's IP
                    int clientPort = receivePacket.getPort();                 // Sender's port
                    int dataLength = receivePacket.getLength();               // Actual data length
                    
                    // Convert received bytes to String
                    String receivedData = new String(
                        receivePacket.getData(), 
                        0, 
                        dataLength
                    ).trim();

                    System.out.println("[UdpHealthServer] ✓ Packet received!");
                    System.out.println("[UdpHealthServer]   From: " + clientAddress.getHostAddress() + ":" + clientPort);
                    System.out.println("[UdpHealthServer]   Data: " + receivedData);
                    System.out.println("[UdpHealthServer]   Size: " + dataLength + " bytes");

                    // 7. Process the health check request and prepare response
                    String responseMessage = processHealthCheck(receivedData, clientAddress, clientPort);
                    
                    // 8. Convert response to bytes
                    byte[] sendBuffer = responseMessage.getBytes();

                    // 9. Create a DatagramPacket to send response back to client
                    // IMPORTANT: We must specify the client's address and port
                    // UDP is connectionless - each packet needs destination info
                    DatagramPacket sendPacket = new DatagramPacket(
                        sendBuffer,           // Data to send
                        sendBuffer.length,    // Length of data
                        clientAddress,        // Destination IP (from received packet)
                        clientPort            // Destination port (from received packet)
                    );

                    // 10. Send the response packet
                    // No connection needed - just send it!
                    socket.send(sendPacket);

                    System.out.println("[UdpHealthServer]   Response: " + responseMessage);
                    System.out.println("[UdpHealthServer]   Response sent successfully!\n");

                } catch (IOException e) {
                    if (running) {
                        System.err.println("[UdpHealthServer] Error processing packet: " + e.getMessage());
                    }
                }
            }

        } catch (SocketException e) {
            System.err.println("[UdpHealthServer] Could not create socket on port " + port + ": " + e.getMessage());
        } finally {
            // Cleanup
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("[UdpHealthServer] Server shut down");
            }
        }
    }

    /**
     * Process the health check request and generate appropriate response
     * 
     * @param request The request message from client
     * @param clientAddress The client's IP address (for logging/tracking)
     * @param clientPort The client's port (for logging/tracking)
     * @return The response message
     */
    private String processHealthCheck(String request, @SuppressWarnings("unused") InetAddress clientAddress, 
                                     @SuppressWarnings("unused") int clientPort) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        // Handle different types of health check requests
        if (request.equalsIgnoreCase("PING") || request.equalsIgnoreCase("HEALTH")) {
            return "STATUS:OK|TIMESTAMP:" + timestamp + "|SERVER:UDP-Health-5002";
        } else if (request.equalsIgnoreCase("STATUS")) {
            // Get system information
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory() / (1024 * 1024);  // MB
            long freeMemory = runtime.freeMemory() / (1024 * 1024);    // MB
            long usedMemory = totalMemory - freeMemory;
            
            return String.format(
                "STATUS:HEALTHY|UPTIME:RUNNING|MEMORY:%dMB/%dMB|TIMESTAMP:%s",
                usedMemory, totalMemory, timestamp
            );
        } else if (request.equalsIgnoreCase("INFO")) {
            return String.format(
                "SERVER:UDP-Health-Check|PORT:%d|PROTOCOL:UDP|VERSION:1.0|TIMESTAMP:%s",
                port, timestamp
            );
        } else if (request.startsWith("ECHO:")) {
            // Echo back whatever comes after "ECHO:"
            String echoData = request.substring(5);
            return "ECHO_RESPONSE:" + echoData + "|TIMESTAMP:" + timestamp;
        } else {
            // Default response - echo the received data
            return "RECEIVED:" + request + "|TIMESTAMP:" + timestamp;
        }
    }

    /**
     * Stop the server gracefully
     */
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * Check if server is running
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running && socket != null && !socket.isClosed();
    }
}
