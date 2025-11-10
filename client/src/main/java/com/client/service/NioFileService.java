package com.client.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * NIO File Service - Connects to NIO File Server (Port 5001)
 * Uses SocketChannel and ByteBuffer for non-blocking file transfer
 */
public class NioFileService {
    private SocketChannel socketChannel;
    
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5001;
    private static final int BUFFER_SIZE = 8192;
    
    /**
     * Connect to NIO File Server
     * @throws IOException If connection fails
     */
    public void connect() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
        socketChannel.configureBlocking(true); // Use blocking mode for simplicity
    }
    
    /**
     * List available files on server
     * @return String containing file list
     * @throws IOException If communication fails
     */
    public String listFiles() throws IOException {
        if (socketChannel == null || !socketChannel.isConnected()) {
            throw new IOException("Not connected to server");
        }
        
        // Send LIST command
        ByteBuffer buffer = ByteBuffer.wrap("LIST\n".getBytes(StandardCharsets.UTF_8));
        socketChannel.write(buffer);
        
        // Read response
        StringBuilder response = new StringBuilder();
        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        
        while (socketChannel.read(readBuffer) > 0) {
            readBuffer.flip();
            while (readBuffer.hasRemaining()) {
                response.append((char) readBuffer.get());
            }
            readBuffer.clear();
            
            // Check if we've received the complete list (ends with newline)
            if (response.toString().endsWith("\n")) {
                break;
            }
        }
        
        return response.toString();
    }
    
    /**
     * Download file from server
     * @param filename Name of file to download
     * @param saveDirectory Directory to save file
     * @return Path to downloaded file
     * @throws IOException If download fails
     */
    public String downloadFile(String filename, String saveDirectory) throws IOException {
        if (socketChannel == null || !socketChannel.isConnected()) {
            throw new IOException("Not connected to server");
        }
        
        // Send GET command
        String command = "GET " + filename + "\n";
        ByteBuffer buffer = ByteBuffer.wrap(command.getBytes(StandardCharsets.UTF_8));
        socketChannel.write(buffer);
        
        // Read response header
        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        socketChannel.read(readBuffer);
        readBuffer.flip();
        
        // Convert to string to find header end
        byte[] headerBytes = new byte[readBuffer.remaining()];
        readBuffer.get(headerBytes);
        String response = new String(headerBytes, StandardCharsets.UTF_8);
        
        if (response.startsWith("ERROR")) {
            throw new IOException(response.trim());
        }
        
        // Find the end of the header line (first newline)
        int headerEnd = response.indexOf('\n');
        if (headerEnd == -1) {
            throw new IOException("Invalid response from server - no header delimiter");
        }
        
        String header = response.substring(0, headerEnd).trim();
        
        // Parse file size from header: "OK <size>"
        String[] parts = header.split("\\s+");
        if (parts.length < 2 || !parts[0].equals("OK")) {
            throw new IOException("Invalid response format: " + header);
        }
        
        long fileSize;
        try {
            fileSize = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid file size in response: " + parts[1]);
        }
        
        // Calculate where file data starts (after the header newline)
        int fileDataStart = headerEnd + 1;
        byte[] initialFileData = response.substring(fileDataStart).getBytes(StandardCharsets.ISO_8859_1);
        
        // Create output file
        File outputFile = new File(saveDirectory, filename);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            // Write initial data that was read with header
            fos.write(initialFileData);
            long bytesReceived = initialFileData.length;
            
            // Read remaining file data
            readBuffer.clear();
            while (bytesReceived < fileSize) {
                int bytesRead = socketChannel.read(readBuffer);
                if (bytesRead == -1) {
                    break;
                }
                
                readBuffer.flip();
                while (readBuffer.hasRemaining() && bytesReceived < fileSize) {
                    fos.write(readBuffer.get());
                    bytesReceived++;
                }
                readBuffer.clear();
            }
        }
        
        return outputFile.getAbsolutePath();
    }
    
    /**
     * Disconnect from server
     */
    public void disconnect() {
        try {
            if (socketChannel != null && socketChannel.isConnected()) {
                socketChannel.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }
    
    /**
     * Check if connected
     * @return true if connected
     */
    public boolean isConnected() {
        return socketChannel != null && socketChannel.isConnected();
    }
}
