package com.network.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO File Server - Demonstrates Non-blocking I/O with Selector pattern
 * 
 * NIO (New I/O) provides high scalability through:
 * - Non-blocking operations (no thread per connection needed)
 * - Selector multiplexing (one thread handles multiple channels)
 * - Buffer-oriented I/O (more efficient than stream-oriented)
 * - Direct memory access for better performance
 * 
 * Key Differences from Traditional I/O:
 * - Traditional: Blocking I/O, thread per connection, stream-oriented
 * - NIO: Non-blocking I/O, selector-based, buffer-oriented
 * - Traditional: Good for few connections with high data transfer
 * - NIO: Excellent for many connections with intermittent data
 * 
 * Use Cases:
 * - File transfer services
 * - High-concurrency servers
 * - Chat servers with many idle connections
 * - Real-time data streaming
 * 
 * This server listens on Port 5001 and handles file requests/transfers.
 */
public class NioFileServer implements Runnable {

    private final int port;
    private Selector selector;              // Multiplexes multiple channels
    private ServerSocketChannel serverChannel;  // Non-blocking server socket
    private volatile boolean running;
    private static final int BUFFER_SIZE = 8192;  // 8KB buffer
    private static final String FILES_DIR = "server_files";  // Directory for files

    /**
     * Constructor
     * @param port The port to bind the NIO server to
     */
    public NioFileServer(int port) {
        this.port = port;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            // Initialize the NIO server
            initializeServer();

            System.out.println("[NioFileServer] NIO File Server started successfully!");
            System.out.println("[NioFileServer] Listening on port " + port);
            System.out.println("[NioFileServer] Mode: Non-blocking I/O (NIO)");
            System.out.println("[NioFileServer] Service: File Transfer / Command Processing");
            System.out.println("[NioFileServer] Files directory: " + FILES_DIR);
            System.out.println("[NioFileServer] Waiting for client connections...\n");

            // Main event loop - the heart of NIO
            while (running) {
                // 1. Wait for events (blocking call, but multiplexes many channels)
                // This blocks until at least one channel is ready for I/O
                // Returns the number of keys whose ready-operation sets were updated
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    continue;  // No channels ready, go back to select()
                }

                // 2. Get the set of keys for channels that are ready for I/O
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                // 3. Process each ready channel
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    
                    // Remove the key from the selected set (important!)
                    // If we don't remove it, select() will keep returning it
                    keyIterator.remove();

                    // 4. Check if the key is valid (channel might have been closed)
                    if (!key.isValid()) {
                        continue;
                    }

                    // 5. Handle different types of I/O events
                    try {
                        if (key.isAcceptable()) {
                            // New client connection is ready to be accepted
                            handleAccept(key);
                        } else if (key.isReadable()) {
                            // Data is ready to be read from a client
                            handleRead(key);
                        }
                    } catch (IOException e) {
                        System.err.println("[NioFileServer] Error handling key: " + e.getMessage());
                        closeConnection(key);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("[NioFileServer] Server error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Initialize the NIO server components
     * - Create ServerSocketChannel
     * - Configure for non-blocking mode
     * - Create Selector
     * - Register channel with selector
     */
    private void initializeServer() throws IOException {
        // 1. Create a Selector - this will monitor multiple channels
        selector = Selector.open();

        // 2. Create a ServerSocketChannel (NIO equivalent of ServerSocket)
        serverChannel = ServerSocketChannel.open();

        // 3. Configure the channel for NON-BLOCKING mode
        // This is CRITICAL - in blocking mode, accept() would block
        // In non-blocking mode, accept() returns immediately (null if no connection)
        serverChannel.configureBlocking(false);

        // 4. Bind the server channel to the port
        serverChannel.bind(new InetSocketAddress(port));

        // 5. Register the channel with the selector
        // OP_ACCEPT: We want to be notified when a new connection arrives
        // This returns a SelectionKey that represents this registration
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        // Create files directory if it doesn't exist
        createFilesDirectory();
    }

    /**
     * Handle OP_ACCEPT event - a new client is connecting
     * 
     * @param key The SelectionKey for the ServerSocketChannel
     */
    private void handleAccept(SelectionKey key) throws IOException {
        // 1. Get the ServerSocketChannel from the key
        ServerSocketChannel server = (ServerSocketChannel) key.channel();

        // 2. Accept the connection (non-blocking - returns immediately)
        // Unlike traditional accept(), this returns null if no connection is pending
        SocketChannel clientChannel = server.accept();

        if (clientChannel != null) {
            // 3. Configure the client channel for non-blocking mode
            clientChannel.configureBlocking(false);

            // 4. Register the client channel with the selector
            // OP_READ: We want to be notified when data is available to read
            // We can attach any object to the key (useful for state management)
            clientChannel.register(selector, SelectionKey.OP_READ);

            System.out.println("[NioFileServer] ✓ New client connected: " 
                + clientChannel.getRemoteAddress());
            System.out.println("[NioFileServer]   Channel configured for non-blocking I/O");

            // Send welcome message
            String welcome = "Welcome to NIO File Server!\n" +
                           "Commands:\n" +
                           "  LIST          - List available files\n" +
                           "  GET <filename> - Download a file\n" +
                           "  INFO          - Server information\n" +
                           "  QUIT          - Disconnect\n" +
                           "> ";
            sendMessage(clientChannel, welcome);
        }
    }

    /**
     * Handle OP_READ event - data is ready to be read from a client
     * 
     * This is where we:
     * - Read data from the SocketChannel into a ByteBuffer
     * - Process the command
     * - Send a response back
     * 
     * @param key The SelectionKey for the SocketChannel with data ready
     */
    private void handleRead(SelectionKey key) throws IOException {
        // 1. Get the SocketChannel from the key
        SocketChannel clientChannel = (SocketChannel) key.channel();

        // 2. Create a ByteBuffer to hold the incoming data
        // ByteBuffer is the fundamental building block of NIO
        // It's a container for data of a specific primitive type
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        // 3. Read data from the channel into the buffer
        // Returns: number of bytes read, -1 if end-of-stream (client disconnected)
        int bytesRead;
        try {
            bytesRead = clientChannel.read(buffer);
        } catch (IOException e) {
            System.out.println("[NioFileServer] Client disconnected abruptly: " 
                + clientChannel.getRemoteAddress());
            closeConnection(key);
            return;
        }

        // 4. Check if client disconnected
        if (bytesRead == -1) {
            System.out.println("[NioFileServer] Client disconnected: " 
                + clientChannel.getRemoteAddress());
            closeConnection(key);
            return;
        }

        // 5. Process the received data
        if (bytesRead > 0) {
            // Flip the buffer from writing mode to reading mode
            // This sets position to 0 and limit to the number of bytes written
            buffer.flip();

            // Convert buffer to string
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            String command = new String(data, StandardCharsets.UTF_8).trim();

            System.out.println("[NioFileServer] Received command from " 
                + clientChannel.getRemoteAddress() + ": " + command);

            // Process the command
            processCommand(clientChannel, command);
        }
    }

    /**
     * Process client commands and send appropriate responses
     * 
     * @param clientChannel The client's SocketChannel
     * @param command The command received from the client
     */
    private void processCommand(SocketChannel clientChannel, String command) throws IOException {
        String response;

        if (command.equalsIgnoreCase("LIST")) {
            // List available files
            response = listFiles();
        } else if (command.toUpperCase().startsWith("GET ")) {
            // Download a file
            String filename = command.substring(4).trim();
            sendFile(clientChannel, filename);
            return;  // sendFile handles the response
        } else if (command.equalsIgnoreCase("INFO")) {
            // Server information
            response = getServerInfo();
        } else if (command.equalsIgnoreCase("QUIT") || command.equalsIgnoreCase("EXIT")) {
            // Disconnect
            sendMessage(clientChannel, "Goodbye!\n");
            clientChannel.close();
            return;
        } else if (command.isEmpty()) {
            // Empty command, just send prompt
            sendMessage(clientChannel, "> ");
            return;
        } else {
            // Unknown command - echo it back
            response = "Unknown command: " + command + "\n" +
                      "Type 'LIST' to see available files or 'INFO' for help.\n";
        }

        sendMessage(clientChannel, response + "> ");
    }

    /**
     * Send a message to the client using NIO
     * 
     * @param channel The client's SocketChannel
     * @param message The message to send
     */
    private void sendMessage(SocketChannel channel, String message) throws IOException {
        // 1. Convert message to bytes
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        // 2. Create a ByteBuffer and put the data into it
        ByteBuffer buffer = ByteBuffer.wrap(messageBytes);

        // 3. Write the buffer to the channel
        // In non-blocking mode, write() might not write all bytes at once
        // We need to loop until all bytes are written
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        System.out.println("[NioFileServer] Sent " + messageBytes.length + " bytes to " 
            + channel.getRemoteAddress());
    }

    /**
     * Send a file to the client
     * 
     * @param channel The client's SocketChannel
     * @param filename The name of the file to send
     * @return Status message
     */
    private String sendFile(SocketChannel channel, String filename) throws IOException {
        Path filePath = Paths.get(FILES_DIR, filename);

        if (!Files.exists(filePath)) {
            String errorMsg = "ERROR: File '" + filename + "' not found.\n";
            sendMessage(channel, errorMsg);
            return errorMsg;
        }

        if (!Files.isRegularFile(filePath)) {
            String errorMsg = "ERROR: '" + filename + "' is not a regular file.\n";
            sendMessage(channel, errorMsg);
            return errorMsg;
        }

        try {
            // Read the entire file into memory (for simplicity)
            // In production, you'd stream large files in chunks
            byte[] fileData = Files.readAllBytes(filePath);
            long fileSize = fileData.length;

            // Send file header
            String header = "FILE: " + filename + "\n" +
                          "SIZE: " + fileSize + " bytes\n" +
                          "---BEGIN FILE---\n";
            sendMessage(channel, header);

            // Send file content using ByteBuffer
            ByteBuffer fileBuffer = ByteBuffer.wrap(fileData);
            while (fileBuffer.hasRemaining()) {
                channel.write(fileBuffer);
            }

            // Send file footer
            String footer = "\n---END FILE---\n";
            sendMessage(channel, footer);

            System.out.println("[NioFileServer] Sent file: " + filename 
                + " (" + fileSize + " bytes) to " + channel.getRemoteAddress());

            return "File sent successfully";

        } catch (IOException e) {
            String errorMsg = "ERROR: Failed to read file: " + e.getMessage() + "\n";
            sendMessage(channel, errorMsg);
            return errorMsg;
        }
    }

    /**
     * List files in the server directory
     * 
     * @return List of files
     */
    private String listFiles() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available files:\n");
        sb.append("================\n");

        try {
            Path dir = Paths.get(FILES_DIR);
            if (!Files.exists(dir)) {
                sb.append("(No files directory found)\n");
                return sb.toString();
            }

            long fileCount = Files.list(dir)
                .filter(Files::isRegularFile)
                .peek(file -> {
                    try {
                        long size = Files.size(file);
                        sb.append("  - ").append(file.getFileName())
                          .append(" (").append(size).append(" bytes)\n");
                    } catch (IOException e) {
                        sb.append("  - ").append(file.getFileName()).append(" (size unknown)\n");
                    }
                })
                .count();

            if (fileCount == 0) {
                sb.append("(No files available)\n");
            } else {
                sb.append("\nTotal: ").append(fileCount).append(" file(s)\n");
            }

        } catch (IOException e) {
            sb.append("ERROR: Could not list files: ").append(e.getMessage()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Get server information
     * 
     * @return Server info string
     */
    private String getServerInfo() {
        return "NIO File Server Information\n" +
               "============================\n" +
               "Port: " + port + "\n" +
               "Mode: Non-blocking I/O (NIO)\n" +
               "Buffer Size: " + BUFFER_SIZE + " bytes\n" +
               "Files Directory: " + FILES_DIR + "\n" +
               "Selector: Active (multiplexing connections)\n" +
               "Protocol: TCP with ByteBuffer\n" +
               "Version: 1.0\n";
    }

    /**
     * Create the files directory if it doesn't exist
     */
    private void createFilesDirectory() throws IOException {
        Path dir = Paths.get(FILES_DIR);
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);
            System.out.println("[NioFileServer] Created files directory: " + FILES_DIR);

            // Create some sample files
            createSampleFile("welcome.txt", "Welcome to the NIO File Server!\nThis is a sample file.");
            createSampleFile("readme.txt", "README\n=====\nUse 'LIST' to see available files.\nUse 'GET <filename>' to download a file.");
            createSampleFile("test.txt", "This is a test file for NIO file transfer.");
        }
    }

    /**
     * Create a sample file
     * 
     * @param filename The name of the file
     * @param content The content of the file
     */
    private void createSampleFile(String filename, String content) {
        try {
            Path filePath = Paths.get(FILES_DIR, filename);
            if (!Files.exists(filePath)) {
                Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
                System.out.println("[NioFileServer] Created sample file: " + filename);
            }
        } catch (IOException e) {
            System.err.println("[NioFileServer] Failed to create sample file: " + e.getMessage());
        }
    }

    /**
     * Close a client connection
     * 
     * @param key The SelectionKey for the connection
     */
    private void closeConnection(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            System.out.println("[NioFileServer] Closing connection: " + channel.getRemoteAddress());
        } catch (IOException e) {
            System.err.println("[NioFileServer] Error getting remote address: " + e.getMessage());
        }
        
        key.cancel();  // Cancel the key
        
        try {
            channel.close();  // Close the channel
        } catch (IOException e) {
            System.err.println("[NioFileServer] Error closing channel: " + e.getMessage());
        }
    }

    /**
     * Cleanup resources on shutdown
     */
    private void cleanup() {
        try {
            if (selector != null && selector.isOpen()) {
                selector.close();
            }
            if (serverChannel != null && serverChannel.isOpen()) {
                serverChannel.close();
            }
            System.out.println("[NioFileServer] Server shut down cleanly");
        } catch (IOException e) {
            System.err.println("[NioFileServer] Error during cleanup: " + e.getMessage());
        }
    }

    /**
     * Stop the server gracefully
     */
    public void stop() {
        running = false;
        if (selector != null) {
            selector.wakeup();  // Wake up the selector from select()
        }
    }

    /**
     * Check if server is running
     * 
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running && serverChannel != null && serverChannel.isOpen();
    }
}
