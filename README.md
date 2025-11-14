# NetHub - Multi-Protocol Network Service Application

A comprehensive Java-based network application demonstrating modern networking concepts through a multi-service architecture. This project implements **TCP**, **UDP**, **NIO**, **multithreading**, and **URL-based networking** to create a scalable service hub with a professional JavaFX GUI client.

## 🎯 Project Overview

**NetHub** is an educational project showcasing five fundamental network programming concepts in a single, cohesive application. It consists of a robust backend with multiple concurrent network services and a feature-rich JavaFX frontend client.

### Key Features
- ✅ **Multi-Protocol Support**: TCP, UDP, and NIO implementations
- ✅ **Concurrent Handling**: Thread pools, event-driven architecture, and stateless design
- ✅ **Modern Java**: Java 17+ with Maven build automation
- ✅ **Professional GUI**: JavaFX 17+ with tabbed interface
- ✅ **Thread-Safe Operations**: Synchronized blocks and concurrent utilities
- ✅ **Production-Ready**: Error handling, graceful shutdown, and resource cleanup

---

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [System Requirements](#system-requirements)
- [Installation & Setup](#installation--setup)
- [Project Structure](#project-structure)
- [Backend Services](#backend-services)
- [Client Application](#client-application)
- [Network Concepts Implemented](#network-concepts-implemented)
- [Usage Guide](#usage-guide)
- [Building & Running](#building--running)
- [Technical Implementation Details](#technical-implementation-details)
- [Troubleshooting](#troubleshooting)

---

## 🏗️ Architecture Overview

The application follows a **layered client-server model**:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│              JavaFX GUI Client (MainController)              │
└────────┬────────────────┬─────────────────┬────────┬────────┘
         │                │                 │        │
    TCP  │           UDP  │            NIO  │ HTTP   │
    Port │           Port │           Port  │        │
    5004 │           5002 │           5001  │        │
         │                │                 │        │
┌────────▼────────────────▼─────────────────▼────────▼────────┐
│                    Service Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │  ChatServer  │  │HealthServer  │  │ FileServer   │       │
│  │   (TCP)      │  │   (UDP)      │  │   (NIO)      │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└────────┬────────────────┬─────────────────┬──────────────────┘
         │                │                 │
    ┌────▼────┐   ┌───────▼────────┐  ┌────▼─────────┐
    │ Thread  │   │  Datagram      │  │  Selector    │
    │  Pool   │   │  Socket        │  │  Pattern     │
    │ (10)    │   │  (Stateless)   │  │  (Scalable)  │
    └─────────┘   └────────────────┘  └──────────────┘
```

### Layers Explained

1. **Presentation Layer**: JavaFX GUI with service-specific tabs
2. **Service Layer**: Four independent network service implementations
3. **Transport Layer**: TCP, UDP, NIO, and HTTP protocols
4. **Backend Layer**: Concurrent servers handling different communication paradigms

---

## 💻 System Requirements

### Prerequisites
- **Java Development Kit (JDK)**: 17 or higher
- **Maven**: 3.6 or higher
- **Operating System**: Windows, macOS, or Linux
- **RAM**: Minimum 512MB (recommended 2GB+)
- **Network**: Ability to bind to ports 5001, 5002, 5004, and 6000

### Verify Installation

```bash
# Check Java version
java -version

# Check Maven version
mvn -version
```

---

## 📦 Installation & Setup

### Step 1: Clone or Extract the Project

```bash
cd /path/to/Assignment\ 2
```

### Step 2: Build the Backend

```bash
cd backend
mvn clean compile
```

### Step 3: Build the Client

```bash
cd ../client
mvn clean compile
```

---

## 📂 Project Structure

```
Assignment 2/
├── README.md                          # This file
├── PROJECT_REPORT.md                  # Detailed technical report
│
├── backend/                           # Backend Services
│   ├── pom.xml                        # Maven configuration
│   ├── test_servers.sh                # Server startup script
│   ├── server_files/                  # File transfer directory
│   │   ├── readme.txt
│   │   ├── test.txt
│   │   └── welcome.txt
│   └── src/
│       ├── main/java/com/network/
│       │   ├── MainServer.java        # Entry point - starts all servers
│       │   ├── tcp/
│       │   │   ├── ChatServer.java    # TCP server with thread pool
│       │   │   └── ClientHandler.java # TCP client handler (Runnable)
│       │   ├── nio/
│       │   │   └── NioFileServer.java # NIO file transfer server
│       │   ├── udp/
│       │   │   ├── UdpHealthServer.java  # UDP health check server
│       │   │   └── UdpHealthClient.java  # UDP test client
│       │   └── util/
│       │       ├── LinkChecker.java      # HTTP URL validator
│       │       └── LinkCheckerClient.java # HTTP client
│       └── test/
│           └── java/com/network/
│               └── AppTest.java       # Unit tests
│
├── client/                            # Client Application
│   ├── pom.xml                        # Maven configuration
│   ├── CLIENT_README.md               # Client documentation
│   └── src/
│       ├── main/java/com/client/
│       │   ├── Launcher.java          # Entry point
│       │   ├── NetHubClient.java      # Main JavaFX application
│       │   ├── controller/
│       │   │   └── MainController.java # UI controller (Tab logic)
│       │   └── service/
│       │       ├── TcpChatService.java # TCP client service
│       │       ├── UdpHealthService.java # UDP client service
│       │       ├── NioFileService.java # NIO client service
│       │       └── LinkCheckerService.java # HTTP validation service
│       └── resources/
│           └── fxml/
│               └── main-view.fxml     # UI layout
│
└── target/                            # Compiled classes (after build)
```

---

## 🌐 Backend Services

### 1. **TCP Chat Server** (Port 5004)

**Protocol**: TCP (Transmission Control Protocol)

**Characteristics**:
- Connection-oriented, reliable, stream-based
- Thread-per-connection model with ExecutorService thread pool
- Synchronous communication
- Guaranteed message delivery and ordering

**Implementation Details**:
- Uses `ServerSocket` for accepting connections
- `Socket` for individual client communication
- `ExecutorService.newFixedThreadPool(10)` for concurrent handling
- Synchronized blocks protect shared client list
- Thread-safe message broadcasting

**Key Components**:
- `ChatServer.java`: Accepts connections and manages client handlers
- `ClientHandler.java`: Handles individual client (Runnable)

**Example Flow**:
```
Client1 connects → Socket created → ClientHandler (Thread 1) spawned
Client2 connects → Socket created → ClientHandler (Thread 2) spawned
Client1 sends "Hello" → Broadcast to all connected clients
```

---

### 2. **UDP Health Check Server** (Port 5002)

**Protocol**: UDP (User Datagram Protocol)

**Characteristics**:
- Connectionless, unreliable, datagram-based
- Stateless request-response model
- Minimal overhead
- No connection establishment required

**Implementation Details**:
- Uses `DatagramSocket` for bidirectional communication
- `DatagramPacket` for message encapsulation
- Command-based protocol: PING, STATUS, INFO
- 5-second timeout handling
- No persistent connection state

**Command Protocol**:
- `PING`: Check server availability (returns "PONG")
- `STATUS`: Get server health information
- `INFO`: Get detailed server information

**Example Flow**:
```
Client sends DatagramPacket("PING") → Server receives → Processes → Sends response
Client (no connection) → Each request is independent
```

---

### 3. **NIO File Transfer Server** (Port 5001)

**Protocol**: NIO (Non-blocking I/O)

**Characteristics**:
- Non-blocking I/O using channels and buffers
- Selector pattern for multiplexing
- Single-threaded event-driven architecture
- Can handle hundreds/thousands of concurrent connections efficiently

**Implementation Details**:
- Uses `ServerSocketChannel` for non-blocking server socket
- `Selector` multiplexes multiple `SocketChannel`s
- `ByteBuffer` for efficient buffer-oriented I/O
- Event-driven architecture processes I/O events
- Serves files from `server_files/` directory

**Key Concepts**:
- **Selector**: Monitors multiple channels for readiness
- **SelectionKey**: Represents registration of channel with selector
- **ByteBuffer**: Non-blocking buffer for data transfer
- **SocketChannel**: Non-blocking socket channel

**Example Flow**:
```
Client 1 connects → Channel registered with Selector
Client 2 connects → Channel registered with Selector
Client 3 connects → Channel registered with Selector
Selector.select() → Returns when any channel is ready
Process ready channels → Return to select() for next event
```

---

### 4. **Link Checker / URL Validator** (HTTP)

**Protocol**: HTTP (via HttpURLConnection)

**Characteristics**:
- Higher-level abstraction over raw sockets
- Uses HTTP protocol for URL validation
- HEAD request method for efficiency
- Timeout handling (5 seconds)

**Implementation Details**:
- `URL` for URL parsing
- `HttpURLConnection` for HTTP requests
- Validates URL format and HTTP status codes
- Response time measurement
- Error handling for network failures

---

## 💻 Client Application

The JavaFX client provides a user-friendly interface to interact with all backend services.

### Features

#### 1. **TCP Chat Tab**
- Real-time message broadcasting
- Username-based authentication
- Multi-threaded message receiving
- Clean disconnect handling
- Thread-safe operations

#### 2. **UDP Health Tab**
- Connectionless communication
- Server availability checking (PING)
- Health status queries (STATUS)
- Server information retrieval (INFO)
- Response time display

#### 3. **NIO File Transfer Tab**
- Browse available files on server
- Download files using NIO
- Directory chooser for save location
- Progress feedback
- Efficient non-blocking I/O

#### 4. **Link Checker Tab**
- URL format validation
- HTTP status code verification
- Response time measurement
- User-friendly error messages
- Batch URL checking support

### Architecture

```
NetHubClient (JavaFX Application)
    └── MainController (UI Logic)
        ├── TcpChatService (Socket communication)
        ├── UdpHealthService (Datagram communication)
        ├── NioFileService (SocketChannel communication)
        └── LinkCheckerService (HttpURLConnection)
```

---

## 🔌 Network Concepts Implemented

### 1. **TCP (Transmission Control Protocol)**
- **OSI Layer**: Transport (Layer 4)
- **Characteristics**: Connection-oriented, reliable, stream-based, ordered
- **Port**: 5004
- **Use Case**: Chat messaging, reliable data transfer
- **Java APIs**: `ServerSocket`, `Socket`, `BufferedReader`, `PrintWriter`
- **Concurrency**: Thread pool (ExecutorService) with 10 threads
- **Thread Safety**: Synchronized blocks on shared client list

**Technical Pattern**:
```java
ServerSocket serverSocket = new ServerSocket(5004);
while (true) {
    Socket clientSocket = serverSocket.accept();  // Blocking
    ExecutorService pool = Executors.newFixedThreadPool(10);
    pool.submit(new ClientHandler(clientSocket));
}
```

### 2. **UDP (User Datagram Protocol)**
- **OSI Layer**: Transport (Layer 4)
- **Characteristics**: Connectionless, unreliable, datagram-based, fast
- **Port**: 5002
- **Use Case**: Health checks, status queries, lightweight communication
- **Java APIs**: `DatagramSocket`, `DatagramPacket`, `InetAddress`
- **Concurrency**: Stateless design, single-threaded handler
- **Protocol**: Custom command-based (PING, STATUS, INFO)

**Technical Pattern**:
```java
DatagramSocket socket = new DatagramSocket(5002);
while (true) {
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    socket.receive(packet);  // Blocking but lightweight
    String command = new String(packet.getData(), 0, packet.getLength());
    // Process and respond
}
```

### 3. **NIO (Non-blocking Input/Output)**
- **OSI Layer**: Transport (Layer 4)
- **Characteristics**: Non-blocking, event-driven, highly scalable
- **Port**: 5001
- **Use Case**: File transfer, high-concurrency servers
- **Java APIs**: `ServerSocketChannel`, `SocketChannel`, `Selector`, `ByteBuffer`
- **Concurrency**: Single-threaded event-driven with Selector
- **Scalability**: Can handle thousands of concurrent connections

**Technical Pattern**:
```java
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.configureBlocking(false);
Selector selector = Selector.open();
serverChannel.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
    selector.select();  // Wait for events (non-blocking multiplexing)
    Set<SelectionKey> keys = selector.selectedKeys();
    // Process ready channels
}
```

### 4. **Multithreading**
- **Concurrency Model**: Thread pool for TCP, Event-driven for NIO
- **Java APIs**: `ExecutorService`, `Executors`, `Thread`, `Runnable`
- **Thread Safety**: `synchronized` keyword, `volatile` fields
- **Pattern**: Thread-per-connection (TCP), Single-threaded (NIO)

**Technical Patterns**:
```java
// Thread Pool Pattern
ExecutorService pool = Executors.newFixedThreadPool(10);
pool.submit(new Runnable() { /* task */ });

// Synchronized Block
synchronized (clients) {
    clients.add(newClient);  // Thread-safe access
}
```

### 5. **URL/HTTP (Hypertext Transfer Protocol)**
- **OSI Layer**: Application (Layer 7)
- **Characteristics**: Request-response, stateless, text-based
- **Use Case**: URL validation, link checking, web connectivity
- **Java APIs**: `URL`, `HttpURLConnection`, `URLConnection`
- **Method**: HEAD request for efficiency
- **Timeout**: 5 seconds for connection and read

**Technical Pattern**:
```java
URL url = new URL("https://example.com");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("HEAD");
conn.setConnectTimeout(5000);
conn.setReadTimeout(5000);
int responseCode = conn.getResponseCode();  // 200, 404, etc.
```

---

## 🚀 Usage Guide

### Starting the Servers

#### Option 1: Run All Servers Manually

```bash
# Terminal 1: Start Backend Servers
cd backend
mvn exec:java -Dexec.mainClass="com.network.MainServer"
```

#### Option 2: Use Provided Shell Script

```bash
cd backend
bash test_servers.sh
```

The backend will start all four services:
- ✓ TCP Chat Server (Port 5004)
- ✓ NIO File Server (Port 5001)
- ✓ UDP Health Server (Port 5002)
- ✓ Link Checker Service (HTTP)

### Starting the Client

```bash
# Terminal 2: Start Client
cd client
mvn javafx:run
```

Or build and run:

```bash
cd client
mvn clean package
java -jar target/client-1.0-SNAPSHOT.jar
```

### Using Each Service

#### **TCP Chat Tab**
1. Enter a username (e.g., "Alice")
2. Click "Connect" button
3. Type a message in the input field
4. Press Enter or click "Send"
5. View messages from all connected users
6. Click "Disconnect" to leave the chat

**Example Usage**:
```
Terminal 1: Server listening on port 5004
Terminal 2: Client 1 "Alice" connects → "Alice joined the chat"
Terminal 3: Client 2 "Bob" connects → "Bob joined the chat"
Terminal 2: Alice sends "Hello Bob"
Terminal 3: Bob receives "Alice: Hello Bob"
```

#### **UDP Health Tab**
1. Click "Initialize" to create UDP socket
2. Choose command:
   - **PING**: Checks if server is alive
   - **STATUS**: Get server health information
   - **INFO**: Get detailed server information
3. Click command button
4. View response in text area
5. Click "Close" to cleanup socket

**Example**:
```
Click PING → "Server is alive: PONG"
Click STATUS → "Server Status: Healthy, Clients: 3, Uptime: 120s"
Click INFO → "NIO File Server, Port: 5001, Files: 3"
```

#### **NIO File Transfer Tab**
1. Click "Connect" to establish NIO connection
2. Click "List Files" to see available files
3. Select a file from the list
4. Choose download location (Directory Chooser)
5. File is transferred using NIO (non-blocking)
6. Success message displays

**Available Files**:
- `readme.txt`
- `test.txt`
- `welcome.txt`

#### **Link Checker Tab**
1. Enter URL in text field (e.g., `https://www.google.com`)
2. Click "Check URL" button
3. Results display:
   - URL validity
   - HTTP status code
   - Response time
   - Error messages (if any)

**Example**:
```
URL: https://www.google.com
Status: 200 OK
Response Time: 145ms
```

---

## 🔨 Building & Running

### Backend Build

```bash
cd backend

# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn clean package

# Run directly
mvn exec:java -Dexec.mainClass="com.network.MainServer"
```

### Client Build

```bash
cd client

# Clean and compile
mvn clean compile

# Run with JavaFX plugin
mvn javafx:run

# Package as JAR
mvn clean package

# Run JAR
java -jar target/client-1.0-SNAPSHOT.jar
```

### Build Both

```bash
# From project root
cd backend && mvn clean package && cd ../client && mvn clean package
```

---

## 🔍 Technical Implementation Details

### TCP Chat Server Flow

```
1. MainServer creates ChatServer(5004)
   ├─ Creates ExecutorService thread pool (10 threads)
   ├─ Creates empty clients list
   └─ Starts listening on port 5004

2. Client connects to port 5004
   ├─ serverSocket.accept() returns Socket
   ├─ Create new ClientHandler(socket)
   ├─ Add to synchronized clients list
   └─ Submit to ExecutorService

3. ClientHandler (in thread pool)
   ├─ Read username from socket input
   ├─ Broadcast "User joined"
   ├─ Listen for messages (blocking read)
   ├─ Broadcast each message to all clients
   └─ On disconnect: remove from list, notify others

4. Thread Safety
   ├─ synchronized(clients) block for adding/removing
   ├─ Broadcast uses iteration with synchronized block
   └─ Prevents concurrent modification exceptions
```

### UDP Health Server Flow

```
1. MainServer creates UdpHealthServer(5002)
   ├─ Creates DatagramSocket on port 5002
   └─ Starts listening

2. Client sends DatagramPacket
   ├─ socket.receive(packet) waits for data
   ├─ Extract command from packet
   ├─ Parse command (PING, STATUS, INFO)
   └─ Prepare response

3. Server processes command
   ├─ If PING → "PONG"
   ├─ If STATUS → server health info
   ├─ If INFO → server details
   └─ Create response packet

4. Server responds
   ├─ socket.send(responsePacket) to sender address
   ├─ No connection state maintained
   └─ Ready for next request
```

### NIO File Server Flow

```
1. MainServer creates NioFileServer(5001)
   ├─ Creates ServerSocketChannel
   ├─ Configures non-blocking mode
   ├─ Creates Selector
   ├─ Registers ServerSocketChannel with OP_ACCEPT
   └─ Starts event loop

2. Client connects
   ├─ selector.select() detects OP_ACCEPT event
   ├─ serverChannel.accept() returns SocketChannel
   ├─ Configure SocketChannel non-blocking
   ├─ Register SocketChannel with OP_READ
   └─ Return to select() immediately

3. Client sends request
   ├─ selector.select() detects OP_READ event
   ├─ Read from SocketChannel into ByteBuffer
   ├─ Parse command (LIST, GET filename)
   ├─ If LIST → send file list
   ├─ If GET → send file contents
   └─ Register OP_WRITE for response

4. Server sends response
   ├─ selector.select() detects OP_WRITE event
   ├─ Write response from ByteBuffer to channel
   ├─ After complete → register OP_READ again
   └─ Continue event loop

Key Advantage: One thread handles thousands of clients efficiently
```

### Thread Safety Mechanisms

```java
// 1. Synchronized Block (TCP Chat)
synchronized (clients) {
    clients.add(newClient);  // Thread-safe add
    for (ClientHandler client : clients) {
        client.sendMessage(message);  // Safe iteration
    }
}

// 2. ExecutorService (TCP Concurrency)
ExecutorService pool = Executors.newFixedThreadPool(10);
pool.submit(clientTask);  // Managed thread execution

// 3. Volatile Flag (NIO Server)
private volatile boolean running;
// Ensures visibility across threads

// 4. Local Variables (Thread-safe by design)
void handleConnection(SocketChannel channel) {
    ByteBuffer buffer = ByteBuffer.allocate(8192);  // Local to thread
    // No sharing needed
}
```

---

## 🐛 Troubleshooting

### Common Issues & Solutions

#### **Port Already in Use**
```
Error: Address already in use
Solution: 
  1. Kill existing process using port:
     - macOS/Linux: lsof -ti:5004 | xargs kill -9
     - Windows: netstat -ano | findstr :5004
  2. Change port number in source code
  3. Wait 60 seconds for OS to release port
```

#### **Client Cannot Connect to Server**
```
Issue: Connection refused error
Solutions:
  1. Verify backend server is running
  2. Check firewall settings allow local connections
  3. Verify correct hostname (use 127.0.0.1 or localhost)
  4. Check port numbers match (5001, 5002, 5004)
  5. Check logs for binding errors
```

#### **JavaFX Module Not Found**
```
Error: module javafx.graphics not found
Solution:
  1. Ensure Java 17+ is installed
  2. Rebuild: mvn clean compile
  3. Check pom.xml has correct JavaFX version
  4. Run: mvn javafx:run
```

#### **Files Not Found in NIO Transfer**
```
Error: File not found during transfer
Solution:
  1. Create server_files/ directory in backend/
  2. Add text files: readme.txt, test.txt, welcome.txt
  3. Files must be in backend/server_files/
  4. Path is hardcoded as "server_files"
```

#### **UDP Timeout**
```
Issue: UDP PING times out
Solutions:
  1. Verify UDP server is running
  2. Check firewall allows UDP port 5002
  3. Increase timeout value in code
  4. Check server logs for errors
```

#### **Maven Build Fails**
```
Error: [ERROR] BUILD FAILURE
Solutions:
  1. Clear Maven cache: mvn clean
  2. Check Java version: java -version
  3. Check Maven version: mvn -version
  4. Update Maven: brew upgrade maven (macOS)
  5. Check internet connection
```

### Debug Mode

Enable detailed logging by adding to MainServer.java:

```java
// Add at start of main method
System.setProperty("java.util.logging.level", "FINE");
Logger logger = Logger.getLogger("com.network");
logger.setLevel(Level.FINE);
```

### Testing Servers

#### Test TCP with telnet:
```bash
telnet localhost 5004
# Type: myusername
# Type messages
# Ctrl+C to exit
```

#### Test UDP with netcat:
```bash
echo "PING" | nc -u localhost 5002
```

#### Test NIO with curl (if HTTP endpoint):
```bash
curl http://localhost:5001/
```

---

## 📊 Performance Characteristics

| Service | Protocol | Scalability | Latency | Throughput | Use Case |
|---------|----------|-------------|---------|-----------|----------|
| **TCP Chat** | TCP | ~100 clients | Low | Medium | Chat, reliable messaging |
| **UDP Health** | UDP | Very high | Very low | High | Health checks, lightweight |
| **NIO File** | NIO | 10,000+ clients | Medium | High | File transfer, many idle clients |
| **Link Checker** | HTTP | Medium | Medium | Medium | URL validation |

---

## 📚 References & Resources

### Java Documentation
- [Java Networking](https://docs.oracle.com/javase/tutorial/networking/index.html)
- [NIO Documentation](https://docs.oracle.com/javase/tutorial/nio/index.html)
- [ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)
- [JavaFX Documentation](https://openjfx.io/openjfx-docs/)

### Related Files
- `PROJECT_REPORT.md`: Comprehensive technical report
- `backend/src/main/java/com/network/`: Backend implementation
- `client/CLIENT_README.md`: Client-specific documentation
- `client/src/main/resources/fxml/main-view.fxml`: UI layout

---

## 👨‍💻 Development

### Key Developers
- Student: Network Programming Assignment 2
- Course: Level 3, Semester 1
- Institution: University

### Version History
- **v1.0** (Current): Multi-protocol implementation with JavaFX GUI

### Contributing

To add new features or improvements:

1. Create a new branch: `git checkout -b feature-name`
2. Make changes with proper commits
3. Test thoroughly on all services
4. Update documentation
5. Submit pull request

---

## 📝 License

This project is created for educational purposes as part of a Network Programming course assignment.

---

## ❓ FAQ

**Q: Can I run multiple clients simultaneously?**
A: Yes! The backend supports concurrent connections. Start multiple client instances or use telnet/netcat to test.

**Q: What's the maximum number of concurrent connections?**
A: TCP: ~100 (limited by 10-thread pool), NIO: 10,000+, UDP: Theoretically unlimited (stateless).

**Q: How do I modify port numbers?**
A: Edit `MainServer.java` and update client connection addresses in `MainController.java`.

**Q: Can I use this over a network (not localhost)?**
A: Yes, change `localhost` to the server's IP address in client connection code.

**Q: How do I add more files to download?**
A: Add .txt files to `backend/server_files/` directory. They'll appear in the NIO file list.

**Q: Can I extend this project?**
A: Absolutely! Suggested extensions:
- Add SSL/TLS encryption
- Implement persistent data storage
- Create REST API layer
- Add authentication/authorization
- Implement web UI with Spring Boot

---

## 📞 Support

For issues or questions:
1. Check `PROJECT_REPORT.md` for technical details
2. Review source code comments
3. Check troubleshooting section above
4. Examine server console output for error messages

---

**Last Updated**: November 2025
**Status**: ✅ Production Ready
