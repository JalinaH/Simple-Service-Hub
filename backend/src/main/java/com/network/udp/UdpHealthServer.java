package com.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UDP Message Processing Server - Demonstrates connectionless communication
 * 
 * UDP (User Datagram Protocol) is a connectionless, fast protocol ideal for:
 * - Quick message processing
 * - Text transformations
 * - Simple calculations
 * - Real-time data where occasional packet loss is acceptable
 * 
 * Key Differences from TCP:
 * - No connection establishment (no accept/connect handshake)
 * - No guaranteed delivery
 * - No ordering guarantee
 * - Faster and more lightweight
 * - Each message is independent (stateless)
 * 
 * This server listens on Port 5002 and processes text commands.
 */
public class UdpHealthServer implements Runnable {

    private final int port;
    private DatagramSocket socket;  // UDP socket (no ServerSocket needed!)
    private volatile boolean running;
    private static final int BUFFER_SIZE = 1024;  // Max size for incoming packets
    private final long startTime;  // Server start time

    /**
     * Constructor
     * @param port The port to bind the UDP server to
     */
    public UdpHealthServer(int port) {
        this.port = port;
        this.running = true;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            // 1. Create and bind DatagramSocket to the specified port
            // Unlike TCP, we don't need ServerSocket - DatagramSocket handles everything
            socket = new DatagramSocket(port);
            
            System.out.println("[UdpHealthServer] UDP Message Processing Server started successfully!");
            System.out.println("[UdpHealthServer] Listening on port " + port);
            System.out.println("[UdpHealthServer] Protocol: UDP (User Datagram Protocol)");
            System.out.println("[UdpHealthServer] Service: Message Processing & Text Transformations");
            System.out.println("[UdpHealthServer] Waiting for requests...\n");

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
     * Process the message request and generate appropriate response
     * 
     * @param request The request message from client
     * @param clientAddress The client's IP address (for logging/tracking)
     * @param clientPort The client's port (for logging/tracking)
     * @return The response message
     */
    private String processHealthCheck(String request, @SuppressWarnings("unused") InetAddress clientAddress, 
                                     @SuppressWarnings("unused") int clientPort) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        // Parse command and argument
        String command = request.toUpperCase();
        String argument = "";
        
        int spaceIndex = request.indexOf(' ');
        if (spaceIndex > 0) {
            command = request.substring(0, spaceIndex).toUpperCase();
            argument = request.substring(spaceIndex + 1);
        }
        
        // Handle different types of requests
        switch (command) {
            case "PING":
            case "HEALTH":
                return "✓ PONG - Server is alive! | Time: " + timestamp;
                
            case "TIME":
                return "⏰ Server Time: " + timestamp;
                
            case "UPTIME":
                long uptimeMs = System.currentTimeMillis() - startTime;
                long seconds = (uptimeMs / 1000) % 60;
                long minutes = (uptimeMs / (1000 * 60)) % 60;
                long hours = (uptimeMs / (1000 * 60 * 60)) % 24;
                return String.format("⏱ Server Uptime: %02d:%02d:%02d", hours, minutes, seconds);
                
            case "ECHO":
                return "📢 Echo: " + argument;
                
            case "REVERSE":
                if (argument.isEmpty()) {
                    return "❌ Error: REVERSE requires text. Usage: REVERSE <text>";
                }
                return "🔄 Reversed: " + new StringBuilder(argument).reverse().toString();
                
            case "UPPERCASE":
            case "UPPER":
                if (argument.isEmpty()) {
                    return "❌ Error: UPPERCASE requires text. Usage: UPPERCASE <text>";
                }
                return "🔠 Uppercase: " + argument.toUpperCase();
                
            case "LOWERCASE":
            case "LOWER":
                if (argument.isEmpty()) {
                    return "❌ Error: LOWERCASE requires text. Usage: LOWERCASE <text>";
                }
                return "🔡 Lowercase: " + argument.toLowerCase();
                
            case "COUNT":
                if (argument.isEmpty()) {
                    return "❌ Error: COUNT requires text. Usage: COUNT <text>";
                }
                int chars = argument.length();
                int words = argument.trim().isEmpty() ? 0 : argument.trim().split("\\s+").length;
                return String.format("📊 Count: %d characters, %d words", chars, words);
                
            case "MATH":
                return processMath(argument);
                
            case "STATUS":
                Runtime runtime = Runtime.getRuntime();
                long totalMemory = runtime.totalMemory() / (1024 * 1024);
                long freeMemory = runtime.freeMemory() / (1024 * 1024);
                long usedMemory = totalMemory - freeMemory;
                return String.format("💻 Status: HEALTHY | Memory: %dMB/%dMB | Port: %d", 
                                   usedMemory, totalMemory, port);
                
            case "INFO":
            case "HELP":
                return "ℹ️ UDP Message Server | Commands:\n" +
                       "  PING/HEALTH - Check if alive\n" +
                       "  TIME - Get server time\n" +
                       "  UPTIME - Server uptime\n" +
                       "  ECHO <text> - Echo back text\n" +
                       "  REVERSE <text> - Reverse text\n" +
                       "  UPPERCASE <text> - Convert to uppercase\n" +
                       "  LOWERCASE <text> - Convert to lowercase\n" +
                       "  COUNT <text> - Count characters and words\n" +
                       "  MATH <expression> - Calculate (e.g., 5+3)\n" +
                       "  STATUS - Server status";
                
            default:
                return "❓ Unknown command: " + request + "\n" +
                       "Type 'HELP' for available commands";
        }
    }
    
    /**
     * Process simple math expressions
     * @param expression The math expression (e.g., "5+3", "10-2", "6*4", "20/5")
     * @return The result or error message
     */
    private String processMath(String expression) {
        if (expression.isEmpty()) {
            return "❌ Error: MATH requires expression. Usage: MATH <number><operator><number>";
        }
        
        try {
            expression = expression.trim().replaceAll("\\s+", "");
            
            // Find operator
            char operator = ' ';
            int operatorIndex = -1;
            
            for (int i = 1; i < expression.length(); i++) {
                char c = expression.charAt(i);
                if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
                    operator = c;
                    operatorIndex = i;
                    break;
                }
            }
            
            if (operatorIndex == -1) {
                return "❌ Error: No operator found. Use +, -, *, /, or %";
            }
            
            double num1 = Double.parseDouble(expression.substring(0, operatorIndex));
            double num2 = Double.parseDouble(expression.substring(operatorIndex + 1));
            double result;
            
            switch (operator) {
                case '+':
                    result = num1 + num2;
                    break;
                case '-':
                    result = num1 - num2;
                    break;
                case '*':
                    result = num1 * num2;
                    break;
                case '/':
                    if (num2 == 0) {
                        return "❌ Error: Division by zero";
                    }
                    result = num1 / num2;
                    break;
                case '%':
                    result = num1 % num2;
                    break;
                default:
                    return "❌ Error: Invalid operator";
            }
            
            // Format result
            if (result == (long) result) {
                return String.format("🧮 Result: %s = %d", expression, (long) result);
            } else {
                return String.format("🧮 Result: %s = %.2f", expression, result);
            }
            
        } catch (NumberFormatException e) {
            return "❌ Error: Invalid number format. Use: MATH <number><operator><number>";
        } catch (Exception e) {
            return "❌ Error: Could not calculate. Check expression format.";
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
