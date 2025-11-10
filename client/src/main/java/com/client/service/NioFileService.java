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
        
        // Read and discard the welcome message (ends with "> ")
        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        StringBuilder welcome = new StringBuilder();
        boolean promptReceived = false;
        
        while (!promptReceived) {
            readBuffer.clear();
            int bytesRead = socketChannel.read(readBuffer);
            
            if (bytesRead > 0) {
                readBuffer.flip();
                while (readBuffer.hasRemaining()) {
                    char c = (char) readBuffer.get();
                    welcome.append(c);
                    
                    // Check for prompt
                    if (welcome.length() >= 2 && 
                        welcome.substring(welcome.length() - 2).equals("> ")) {
                        promptReceived = true;
                        break;
                    }
                }
            }
        }
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
        
        // Read response until we get the prompt ">"
        StringBuilder response = new StringBuilder();
        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        
        boolean promptReceived = false;
        while (!promptReceived) {
            readBuffer.clear();
            int bytesRead = socketChannel.read(readBuffer);
            
            if (bytesRead == -1) {
                throw new IOException("Server closed connection");
            }
            
            if (bytesRead > 0) {
                readBuffer.flip();
                while (readBuffer.hasRemaining()) {
                    char c = (char) readBuffer.get();
                    response.append(c);
                    
                    // Check if we've received the prompt at the end
                    if (response.length() >= 2 && 
                        response.substring(response.length() - 2).equals("> ")) {
                        promptReceived = true;
                        break;
                    }
                }
            }
        }
        
        // Remove the prompt from the response
        String result = response.toString();
        if (result.endsWith("> ")) {
            result = result.substring(0, result.length() - 2);
        }
        
        return result.trim();
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
        
        // Read the entire response into a StringBuilder
        StringBuilder response = new StringBuilder();
        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        
        // Read until we get "---END FILE---" or an error
        boolean endMarkerFound = false;
        while (!endMarkerFound) {
            readBuffer.clear();
            int bytesRead = socketChannel.read(readBuffer);
            
            if (bytesRead == -1) {
                throw new IOException("Server closed connection");
            }
            
            if (bytesRead > 0) {
                readBuffer.flip();
                byte[] data = new byte[readBuffer.remaining()];
                readBuffer.get(data);
                response.append(new String(data, StandardCharsets.ISO_8859_1));
                
                // Check if we have the end marker
                if (response.toString().contains("---END FILE---")) {
                    endMarkerFound = true;
                }
                
                // Check for error
                if (response.toString().startsWith("ERROR")) {
                    // Read until prompt
                    if (response.toString().contains("> ")) {
                        String errorMsg = response.toString();
                        int promptIdx = errorMsg.indexOf("> ");
                        errorMsg = errorMsg.substring(0, promptIdx).trim();
                        throw new IOException(errorMsg);
                    }
                }
            }
        }
        
        String fullResponse = response.toString();
        
        // Parse the response format:
        // FILE: welcome.txt
        // SIZE: 54 bytes
        // ---BEGIN FILE---
        // <file content>
        // ---END FILE---
        
        if (!fullResponse.startsWith("FILE:")) {
            throw new IOException("Invalid response format from server");
        }
        
        // Extract file size from SIZE line
        int sizeStart = fullResponse.indexOf("SIZE: ") + 6;
        int sizeEnd = fullResponse.indexOf(" bytes", sizeStart);
        if (sizeStart == -1 || sizeEnd == -1) {
            throw new IOException("Could not parse file size from response");
        }
        long fileSize = Long.parseLong(fullResponse.substring(sizeStart, sizeEnd).trim());
        
        // Extract file content between markers
        int contentStart = fullResponse.indexOf("---BEGIN FILE---") + 16;
        int contentEnd = fullResponse.indexOf("\n---END FILE---");
        
        if (contentStart == -1 || contentEnd == -1) {
            throw new IOException("Could not find file content markers");
        }
        
        // Extract file content (skip the newline after BEGIN marker)
        if (contentStart < fullResponse.length() && fullResponse.charAt(contentStart) == '\n') {
            contentStart++;
        }
        
        String fileContent = fullResponse.substring(contentStart, contentEnd);
        byte[] fileBytes = fileContent.getBytes(StandardCharsets.ISO_8859_1);
        
        // Create output file
        File outputFile = new File(saveDirectory, filename);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(fileBytes);
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
