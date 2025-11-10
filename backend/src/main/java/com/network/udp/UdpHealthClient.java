package com.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 * UDP Client for testing the Health Check Server
 * 
 * This client demonstrates how to send and receive UDP packets.
 * Unlike TCP, there's no connection - just send/receive datagrams.
 */
public class UdpHealthClient {

    private static final int SERVER_PORT = 5002;
    private static final String SERVER_HOST = "localhost";
    private static final int TIMEOUT_MS = 5000;  // 5 second timeout
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        printBanner();

        try (DatagramSocket clientSocket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            // Set timeout so receive() doesn't block forever
            clientSocket.setSoTimeout(TIMEOUT_MS);

            // Get server address
            InetAddress serverAddress = InetAddress.getByName(SERVER_HOST);

            System.out.println("UDP Health Check Client");
            System.out.println("Connected to: " + SERVER_HOST + ":" + SERVER_PORT);
            System.out.println();
            System.out.println("Available commands:");
            System.out.println("  PING          - Simple health check");
            System.out.println("  HEALTH        - Health status check");
            System.out.println("  STATUS        - Detailed server status");
            System.out.println("  INFO          - Server information");
            System.out.println("  ECHO:<text>   - Echo message");
            System.out.println("  <any text>    - Send custom message");
            System.out.println("  quit          - Exit client");
            System.out.println();

            boolean running = true;
            while (running) {
                System.out.print("Enter command: ");
                String command = scanner.nextLine().trim();

                if (command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("exit")) {
                    running = false;
                    System.out.println("Goodbye!");
                    continue;
                }

                if (command.isEmpty()) {
                    continue;
                }

                try {
                    // Send request
                    long startTime = System.currentTimeMillis();
                    sendMessage(clientSocket, command, serverAddress, SERVER_PORT);

                    // Receive response
                    String response = receiveMessage(clientSocket);
                    long endTime = System.currentTimeMillis();
                    long latency = endTime - startTime;

                    System.out.println("✓ Response received:");
                    System.out.println("  " + response);
                    System.out.println("  Latency: " + latency + "ms");
                    System.out.println();

                } catch (SocketTimeoutException e) {
                    System.err.println("✗ Timeout: No response from server within " + TIMEOUT_MS + "ms");
                    System.err.println("  Is the server running on port " + SERVER_PORT + "?");
                    System.out.println();
                } catch (IOException e) {
                    System.err.println("✗ Error: " + e.getMessage());
                    System.out.println();
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to create UDP client: " + e.getMessage());
            System.err.println("Details: " + e.getClass().getName());
        }
    }

    /**
     * Send a UDP message to the server
     */
    private static void sendMessage(DatagramSocket socket, String message, 
                                    InetAddress address, int port) throws IOException {
        byte[] sendBuffer = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(
            sendBuffer,
            sendBuffer.length,
            address,
            port
        );
        socket.send(sendPacket);
        System.out.println("→ Sent: " + message);
    }

    /**
     * Receive a UDP message from the server
     */
    private static String receiveMessage(DatagramSocket socket) throws IOException {
        byte[] receiveBuffer = new byte[BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);

        return new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
    }

    private static void printBanner() {
        System.out.println("============================================================");
        System.out.println("       UDP HEALTH CHECK CLIENT");
        System.out.println("============================================================");
        System.out.println();
    }
}
