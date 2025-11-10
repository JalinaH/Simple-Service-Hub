# Complete Network Programming Implementation
## TCP + UDP + NIO (All Three Protocols)

---

## 📋 Complete Implementation Overview

This project demonstrates **three fundamental networking paradigms** in Java:

1. **TCP (Transmission Control Protocol)** - Connection-oriented, reliable, blocking I/O
2. **UDP (User Datagram Protocol)** - Connectionless, fast, unreliable
3. **NIO (Non-blocking I/O)** - Selector-based, scalable, buffer-oriented

---

## 🎯 Implementation Status: ✅ COMPLETE

### ✅ **Task 1: TCP Server (Port 5000)**
- ✅ ServerSocket with accept() loop
- ✅ ExecutorService thread pool (10 threads)
- ✅ ClientHandler implements Runnable
- ✅ synchronized blocks for thread safety
- ✅ Chat functionality with broadcasting

### ✅ **Task 2: UDP Server (Port 5002)**
- ✅ DatagramSocket bound to port
- ✅ while(true) loop for continuous listening
- ✅ DatagramPacket for receive/send operations
- ✅ Echo service with health check responses
- ✅ InetAddress for client addressing

### ✅ **Task 3: NIO Server (Port 5001)**
- ✅ ServerSocketChannel in non-blocking mode
- ✅ Selector for I/O multiplexing
- ✅ OP_ACCEPT and OP_READ event handling
- ✅ ByteBuffer for data transfer
- ✅ File transfer service
- ✅ SocketChannel management

---

## 🏗️ Architecture Comparison

```
┌─────────────────┬──────────────────┬──────────────────┬──────────────────┐
│   Aspect        │   TCP (5000)     │   UDP (5002)     │   NIO (5001)     │
├─────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Connection      │ Connection-      │ Connectionless   │ Connection-      │
│                 │ oriented         │                  │ oriented         │
├─────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Reliability     │ Guaranteed       │ Best effort      │ Guaranteed       │
│                 │ delivery         │ (may lose)       │ delivery         │
├─────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Ordering        │ Ordered          │ Unordered        │ Ordered          │
├─────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ I/O Model       │ Blocking         │ Blocking         │ Non-blocking     │
├─────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Threading       │ Thread per       │ Single thread    │ Selector-based   │
│                 │ connection       │ (one socket)     │ (single thread)  │
├─────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Scalability     │ Medium           │ High             │ Very High        │
│                 │ (~100s clients)  │ (~1000s)         │ (~10000s)        │
├─────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Overhead        │ High             │ Low              │ Medium           │
├─────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Use Cases       │ - Chat           │ - Health checks  │ - File transfer  │
│                 │ - File transfer  │ - Status monitor │ - High traffic   │
│                 │ - HTTP           │ - Gaming         │ - Web servers    │
│                 │ - Email          │ - Streaming      │ - Proxies        │
├─────────────────┼──────────────────┼──────────────────┼──────────────────┤
│ Key Classes     │ ServerSocket     │ DatagramSocket   │ ServerSocket-    │
│                 │ Socket           │ DatagramPacket   │ Channel          │
│                 │ ExecutorService  │ InetAddress      │ Selector         │
│                 │                  │                  │ ByteBuffer       │
│                 │                  │                  │ SelectionKey     │
└─────────────────┴──────────────────┴──────────────────┴──────────────────┘
```

---

## 📁 Project Structure

```
backend/
├── src/main/java/com/network/
│   ├── MainServer.java                 # Entry point - starts all servers
│   ├── tcp/
│   │   ├── ChatServer.java             # TCP server with thread pool
│   │   └── ClientHandler.java          # Runnable for each client
│   ├── udp/
│   │   ├── UdpHealthServer.java        # UDP health check server
│   │   └── UdpHealthClient.java        # UDP client for testing
│   └── nio/
│       └── NioFileServer.java          # NIO file transfer server
├── server_files/                       # Files served by NIO server
│   ├── welcome.txt
│   ├── readme.txt
│   └── test.txt
└── pom.xml
```

---

## 🚀 Running the Servers

### **Compile**
```bash
cd backend
mvn clean compile
```

### **Run All Servers**
```bash
# Option 1: Using Maven (if network available)
mvn exec:java -Dexec.mainClass="com.network.MainServer"

# Option 2: Using Java directly
java -cp target/classes com.network.MainServer
```

### **Expected Output**
```
============================================================
       SIMPLE SERVICE HUB - NETWORK SERVER
============================================================

[MainServer] Starting TCP Chat Server...
[MainServer] ✓ TCP Chat Server initialized on port 5000

[MainServer] Starting NIO File Transfer Server...
[MainServer] ✓ NIO File Server initialized on port 5001

[MainServer] Starting UDP Health Check Server...
[MainServer] ✓ UDP Health Check Server initialized on port 5002

============================================================
[MainServer] 🚀 ALL SERVICES ARE RUNNING!

[MainServer] Service Overview:
[MainServer]   Port 5000 - TCP Chat Server (blocking, multithreaded)
[MainServer]   Port 5001 - NIO File Server (non-blocking, selector-based)
[MainServer]   Port 5002 - UDP Health Server (connectionless, fast)

[MainServer] Press Ctrl+C to stop all servers
============================================================

[ChatServer] TCP Server started successfully!
[ChatServer] Listening on port 5000
[NioFileServer] NIO File Server started successfully!
[NioFileServer] Listening on port 5001
[UdpHealthServer] UDP Health Check Server started successfully!
[UdpHealthServer] Listening on port 5002
```

---

## 🧪 Testing Each Server

### **1. Test TCP Chat Server (Port 5000)**

**Using Telnet:**
```bash
# Terminal 1
telnet localhost 5000

# Terminal 2
telnet localhost 5000

# Terminal 3
telnet localhost 5000
```

**Commands:**
- Type any message → broadcasts to all other clients
- `/clients` → see number of active clients
- `/quit` → disconnect

**Demonstrates:**
- Multithreading (multiple clients simultaneously)
- Thread-safe broadcasting
- ExecutorService thread pool

---

### **2. Test NIO File Server (Port 5001)**

**Using Telnet:**
```bash
telnet localhost 5001
```

**Commands:**
```
LIST               # List available files
GET welcome.txt    # Download a file
INFO              # Server information
QUIT              # Disconnect
```

**Using Netcat:**
```bash
echo "LIST" | nc localhost 5001
echo "GET readme.txt" | nc localhost 5001
```

**Demonstrates:**
- Non-blocking I/O with Selector
- ByteBuffer operations
- OP_ACCEPT and OP_READ events
- File transfer over TCP using NIO

---

### **3. Test UDP Health Server (Port 5002)**

**Using the provided UDP Client:**
```bash
# Run the UDP client
java -cp target/classes com.network.udp.UdpHealthClient
```

**Commands:**
```
PING              # Simple health check
HEALTH            # Health status
STATUS            # Detailed server status
INFO              # Server information
ECHO:Hello        # Echo message
quit              # Exit client
```

**Using Netcat (UDP mode):**
```bash
# Send UDP packet
echo "PING" | nc -u localhost 5002

# Or interactive mode
nc -u localhost 5002
> PING
> STATUS
> INFO
```

**Demonstrates:**
- Connectionless communication
- DatagramPacket creation
- InetAddress usage
- Fast request-response pattern

---

## 🔑 Key Implementation Details

### **1. TCP Server - Multithreading**

```java
// Server loop (non-blocking for new connections)
while (true) {
    Socket clientSocket = serverSocket.accept();  // Blocks for new client
    ClientHandler handler = new ClientHandler(clientSocket, this);
    
    synchronized (clients) {  // Thread-safe access
        clients.add(handler);
    }
    
    pool.submit(handler);  // Non-blocking submission
}

// Client handler runs in separate thread
class ClientHandler implements Runnable {
    public void run() {
        // Blocks only THIS thread, not others
        String line = in.readLine();
        server.broadcast(line, this);
    }
}
```

**Key Concepts:**
- ExecutorService manages thread pool
- Runnable for concurrent execution
- synchronized for shared resource protection

---

### **2. UDP Server - Connectionless**

```java
DatagramSocket socket = new DatagramSocket(5002);

while (true) {
    // Prepare to receive
    byte[] buffer = new byte[1024];
    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
    
    // Block until packet arrives
    socket.receive(receivePacket);
    
    // Extract sender info (no connection!)
    InetAddress clientAddress = receivePacket.getAddress();
    int clientPort = receivePacket.getPort();
    
    // Send response to specific address
    byte[] responseData = "OK".getBytes();
    DatagramPacket sendPacket = new DatagramPacket(
        responseData, 
        responseData.length, 
        clientAddress,   // Where to send
        clientPort       // Which port
    );
    
    socket.send(sendPacket);
}
```

**Key Concepts:**
- No accept() - just receive packets
- Each packet independent (stateless)
- Must address each response explicitly

---

### **3. NIO Server - Non-blocking**

```java
// Setup
Selector selector = Selector.open();
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.configureBlocking(false);  // NON-BLOCKING!
serverChannel.register(selector, SelectionKey.OP_ACCEPT);

// Main loop - handles ALL clients
while (true) {
    selector.select();  // Block until ANY channel ready
    
    Set<SelectionKey> keys = selector.selectedKeys();
    for (SelectionKey key : keys) {
        if (key.isAcceptable()) {
            // New client
            SocketChannel client = serverChannel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        }
        else if (key.isReadable()) {
            // Data ready
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            int bytesRead = client.read(buffer);  // Non-blocking!
            
            if (bytesRead > 0) {
                buffer.flip();
                // Process data...
            }
        }
    }
}
```

**Key Concepts:**
- One thread handles many channels
- Selector monitors all registered channels
- ByteBuffer for data transfer
- SelectionKey indicates ready operations

---

## 📊 Performance Characteristics

### **TCP Server (Port 5000)**
- **Concurrency:** Up to 10 simultaneous clients (thread pool size)
- **Scalability:** Medium (limited by thread count)
- **Latency:** Low (direct socket I/O)
- **Memory:** High (thread per connection)
- **Best For:** Real-time chat, critical messages

### **UDP Server (Port 5002)**
- **Concurrency:** Thousands (single thread, stateless)
- **Scalability:** Very High (no connection overhead)
- **Latency:** Very Low (no handshake)
- **Memory:** Very Low (no connection state)
- **Best For:** Health checks, monitoring, gaming

### **NIO Server (Port 5001)**
- **Concurrency:** Tens of thousands (selector-based)
- **Scalability:** Excellent (single thread multiplexing)
- **Latency:** Low (efficient buffer management)
- **Memory:** Low (minimal overhead per connection)
- **Best For:** File transfers, high-traffic servers

---

## 🎓 Learning Outcomes

### **TCP + Multithreading**
✅ ServerSocket and Socket usage  
✅ ExecutorService thread pool  
✅ Runnable interface implementation  
✅ synchronized keyword for thread safety  
✅ Race condition prevention  
✅ Resource management (try-with-resources)  

### **UDP + Datagram Communication**
✅ DatagramSocket creation  
✅ DatagramPacket construction  
✅ InetAddress for addressing  
✅ Connectionless communication  
✅ Stateless protocol design  
✅ Fast request-response patterns  

### **NIO + Selector Pattern**
✅ ServerSocketChannel setup  
✅ Non-blocking configuration  
✅ Selector for I/O multiplexing  
✅ SelectionKey and operation types  
✅ ByteBuffer operations (flip, clear, etc.)  
✅ Event-driven architecture  
✅ High-scalability design  

---

## 🔍 Code Statistics

```
Total Java Files: 6
Total Lines of Code: ~1200
```

**Breakdown:**
- MainServer.java: ~80 lines
- TCP Server: ~200 lines (ChatServer + ClientHandler)
- UDP Server: ~200 lines (UdpHealthServer + UdpHealthClient)
- NIO Server: ~520 lines (NioFileServer)
- Comments/Documentation: ~200 lines

---

## 🐛 Troubleshooting

### **Port Already in Use**
```
Error: Address already in use
```

**Solution:**
```bash
# Find process using the port
lsof -i :5000   # or 5001, 5002

# Kill the process
kill -9 <PID>

# Or use different ports in MainServer.java
```

### **Connection Refused**
```
Error: Connection refused
```

**Check:**
1. Is the server running?
2. Correct port number?
3. Firewall blocking connections?

### **UDP Not Receiving Responses**
```
Timeout: No response from server
```

**Check:**
1. UDP server running on port 5002?
2. Using `-u` flag with netcat?
3. Firewall allowing UDP?

---

## 📚 Additional Resources

### **Testing Tools**
- **Telnet:** `telnet localhost <port>` (TCP)
- **Netcat:** `nc localhost <port>` (TCP), `nc -u localhost <port>` (UDP)
- **Custom Client:** `java -cp target/classes com.network.udp.UdpHealthClient`

### **Monitoring**
```bash
# Check if ports are listening
lsof -i :5000
lsof -i :5001
lsof -i :5002

# Monitor network traffic
sudo tcpdump -i lo0 port 5000  # TCP
sudo tcpdump -i lo0 port 5002  # UDP
```

---

## 🎯 Assignment Completion

### **All Tasks Implemented:**
1. ✅ TCP Server with ServerSocket and accept() loop
2. ✅ Multithreading with ExecutorService
3. ✅ ClientHandler implementing Runnable
4. ✅ Thread-safe operations with synchronized
5. ✅ UDP Server with DatagramSocket
6. ✅ DatagramPacket for send/receive
7. ✅ Echo service with client addressing
8. ✅ NIO Server with ServerSocketChannel
9. ✅ Non-blocking configuration
10. ✅ Selector for I/O events
11. ✅ OP_ACCEPT and OP_READ handling
12. ✅ ByteBuffer for data transfer
13. ✅ File transfer functionality

### **Extra Features:**
- ✅ Complete UDP client for testing
- ✅ File management in NIO server
- ✅ Command processing in all servers
- ✅ Comprehensive error handling
- ✅ Extensive documentation
- ✅ Clean resource management

---

**Implementation Date:** November 10, 2025  
**Course:** Network Programming (L3S1)  
**Assignment:** Simple Service Hub - Complete Implementation  
**Status:** ✅ FULLY COMPLETE - All Three Protocols Implemented
