# NetHub - Java Network Programming Project

![Java](https://img.shields.io/badge/Java-17-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.14-blue)
![Maven](https://img.shields.io/badge/Maven-3.6+-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

A comprehensive Java networking application demonstrating **5 core networking concepts** through a client-server architecture with a modern JavaFX GUI.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Usage Guide](#usage-guide)
- [Networking Concepts](#networking-concepts)
- [Team Members](#team-members)
- [Screenshots](#screenshots)
- [Troubleshooting](#troubleshooting)
- [Documentation](#documentation)
- [License](#license)

## 🎯 Overview

**NetHub** is a multi-service network application built for the IN3111 Network Programming course. It demonstrates industry-standard networking patterns and Java I/O APIs through practical implementations:

- **TCP Chat Server** - Connection-oriented messaging with multithreading
- **NIO File Server** - Non-blocking file transfer with Selector pattern
- **UDP Message Processor** - Connectionless text processing and calculations
- **JavaFX Client** - Modern GUI connecting to all services
- **HTTP Link Checker** - URL validation using HttpURLConnection

### Course Information
- **Course**: IN3111 - Network Programming
- **Level**: Level 3, Semester 1
- **Project**: Assignment 2 - Java Service Hub
- **Repository**: Simple-Service-Hub

## ✨ Features

### Backend Services (Port-based)

#### 1. TCP Chat Server (Port 5004)
- Real-time multi-client chat
- Username-based identification
- Message broadcasting
- Thread pool management (10 workers)
- Synchronized client list
- Special commands: `/quit`, `/clients`

#### 2. NIO File Transfer Server (Port 5001)
- Non-blocking I/O with Selector
- File listing and download
- Efficient buffer-based transfers
- Single-thread handles multiple connections
- Commands: `LIST`, `GET <filename>`, `INFO`, `QUIT`

#### 3. UDP Message Processing Server (Port 5002)
- Connectionless fast communication
- Text transformations (REVERSE, UPPERCASE, LOWERCASE)
- Character/word counting
- Math calculator (supports +, -, *, /, %)
- Server status and uptime
- Echo service

### Frontend (JavaFX Client)

#### Modern Tabbed Interface
- **TCP Chat Tab** - Connect, send/receive messages
- **UDP Messages Tab** - Quick commands + custom input
- **NIO File Transfer Tab** - List and download files
- **Link Checker Tab** - Validate URLs and check HTTP status

#### User Experience
- Real-time status updates
- Color-coded buttons
- Professional styling with CSS
- Error handling with user-friendly alerts
- Progress feedback

## 🏗️ Architecture

```
NetHub Project
│
├── Backend (Java 8+)
│   ├── MainServer.java          # Entry point, starts all services
│   ├── TCP Server               # Port 5004 (ChatServer + ClientHandler)
│   ├── NIO Server               # Port 5001 (NioFileServer)
│   └── UDP Server               # Port 5002 (UdpHealthServer)
│
└── Client (Java 17 + JavaFX)
    ├── NetHubClient.java        # JavaFX application entry
    ├── Controllers              # UI logic
    ├── Services                 # Network communication
    │   ├── TcpChatService
    │   ├── UdpHealthService
    │   ├── NioFileService
    │   └── LinkCheckerService
    └── Views (FXML)             # UI layout
```

### Design Patterns
- **Service Layer Pattern** - Separation of network logic from UI
- **Observer Pattern** - Callback-based UI updates
- **Thread Pool Pattern** - ExecutorService for concurrent handling
- **Selector Pattern** - NIO multiplexing for scalability

## 🛠️ Technologies Used

### Core Java
- **Java 17** (Client) / **Java 8+** (Backend)
- **Maven** - Build automation and dependency management
- **Java Modules** - Modular application structure

### Networking APIs
- `java.net.Socket` - TCP client connections
- `java.net.ServerSocket` - TCP server listening
- `java.net.DatagramSocket` - UDP communication
- `java.net.DatagramPacket` - UDP packet handling
- `java.nio.channels.Selector` - Non-blocking I/O multiplexing
- `java.nio.channels.SocketChannel` - NIO socket operations
- `java.nio.ByteBuffer` - Efficient byte operations
- `java.net.URL` - URL parsing and validation
- `java.net.HttpURLConnection` - HTTP operations

### Concurrency
- `java.util.concurrent.ExecutorService` - Thread pool management
- `synchronized` blocks - Thread-safe operations
- `Thread` - Background task execution
- `Platform.runLater()` - JavaFX thread-safe UI updates

### Frontend
- **JavaFX 17.0.14** - Modern UI framework
- **FXML** - Declarative UI design
- **CSS** - Styling and theming

## 📁 Project Structure

```
Assignment 2/
│
├── backend/                      # Server-side application
│   ├── pom.xml                   # Maven configuration
│   ├── server_files/             # Files available for NIO download
│   │   ├── welcome.txt
│   │   ├── test.txt
│   │   └── readme.txt
│   └── src/main/java/com/network/
│       ├── MainServer.java       # Main entry point
│       ├── tcp/
│       │   ├── ChatServer.java
│       │   └── ClientHandler.java
│       ├── nio/
│       │   └── NioFileServer.java
│       └── udp/
│           ├── UdpHealthServer.java
│           └── UdpHealthClient.java (test client)
│
├── client/                       # JavaFX client application
│   ├── pom.xml                   # Maven configuration
│   └── src/main/
│       ├── java/com/client/
│       │   ├── NetHubClient.java
│       │   ├── Launcher.java
│       │   ├── controller/
│       │   │   └── MainController.java
│       │   └── service/
│       │       ├── TcpChatService.java
│       │       ├── UdpHealthService.java
│       │       ├── NioFileService.java
│       │       └── LinkCheckerService.java
│       └── resources/
│           └── fxml/
│               └── main-view.fxml
│
├── README.md                     # This file
├── PROJECT_SUMMARY.md            # Detailed technical documentation
├── QUICKSTART.md                 # Quick start guide
└── CLIENT_README.md              # Client-specific documentation
```

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK) 17** or higher
- **Maven 3.6+**
- **Terminal/Command Prompt**

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/JalinaH/Simple-Service-Hub.git
   cd "Assignment 2"
   ```

2. **Compile Backend:**
   ```bash
   cd backend
   mvn clean compile
   ```

3. **Compile Client:**
   ```bash
   cd ../client
   mvn clean compile
   ```

### Running the Application

#### Step 1: Start Backend Services

Open a terminal and run:

```bash
cd backend
mvn exec:java -Dexec.mainClass="com.network.MainServer"
```

**Expected Output:**
```
============================================================
       SIMPLE SERVICE HUB - NETWORK SERVER
============================================================

[MainServer] Starting TCP Chat Server...
[MainServer] ✓ TCP Chat Server initialized on port 5004

[MainServer] Starting NIO File Transfer Server...
[MainServer] ✓ NIO File Server initialized on port 5001

[MainServer] Starting UDP Health Check Server...
[MainServer] ✓ UDP Health Check Server initialized on port 5002

============================================================
[MainServer] 🚀 ALL SERVICES ARE RUNNING!

[MainServer] Service Overview:
[MainServer]   Port 5004 - TCP Chat Server (blocking, multithreaded)
[MainServer]   Port 5001 - NIO File Server (non-blocking, selector-based)
[MainServer]   Port 5002 - UDP Health Server (connectionless, fast)

[MainServer] Press Ctrl+C to stop all servers
============================================================
```

**Keep this terminal running!**

#### Step 2: Start JavaFX Client

Open a **new terminal** and run:

```bash
cd client
mvn javafx:run
```

The NetHub Client GUI will launch with 4 tabs.

## 📖 Usage Guide

### TCP Chat

1. Navigate to the **TCP Chat** tab
2. Enter a username (e.g., "Alice")
3. Click **Connect**
4. Type messages in the text field
5. Click **Send** or press Enter
6. Open multiple client instances to test multi-user chat

**Tips:**
- Use `/quit` to disconnect gracefully
- Use `/clients` to see active client count

### UDP Message Processing

1. Navigate to the **UDP Messages** tab
2. Click **Initialize** to create UDP socket
3. Use quick command buttons:
   - **PING** - Check if server is alive
   - **TIME** - Get current server time
   - **UPTIME** - Server uptime duration
   - **STATUS** - Memory and server status
   - **INFO** - Show all commands
4. Or type custom commands:
   - `ECHO hello world` - Echo back text
   - `REVERSE hello` - Returns "olleh"
   - `UPPERCASE test` - Returns "TEST"
   - `LOWERCASE TEST` - Returns "test"
   - `COUNT the quick brown fox` - Counts characters and words
   - `MATH 25*4` - Returns 100
5. Click **Send** or press Enter

**Supported Math Operations:**
- Addition: `MATH 5+3`
- Subtraction: `MATH 10-2`
- Multiplication: `MATH 6*4`
- Division: `MATH 20/5`
- Modulus: `MATH 10%3`

### NIO File Transfer

1. Navigate to the **NIO File Transfer** tab
2. Click **Connect** to establish connection
3. Click **List Files** to see available files
4. Enter filename in the text field (e.g., `welcome.txt`)
5. Click **Download**
6. Choose destination folder in dialog
7. File is downloaded using NIO ByteBuffer

**Note:** Files must exist in `backend/server_files/` directory.

### Link Checker

1. Navigate to the **Link Checker** tab
2. Enter a URL (e.g., `https://www.google.com`)
3. Click **Check Link** or press Enter
4. View results:
   - ✓ Valid (Status 200-399)
   - ✗ Invalid (Status 400+, errors)
   - Response time in milliseconds
   - HTTP status code and description

**Test URLs:**
- Valid: `https://www.google.com`
- Valid: `https://github.com`
- Invalid host: `https://thiswebsitedoesnotexist12345.com`
- Not found: `https://www.google.com/nonexistent`

## 🔬 Networking Concepts

### 1. TCP/IP Communication

**Implementation:** `ChatServer.java`, `ClientHandler.java`, `TcpChatService.java`

**Key APIs:**
- `ServerSocket` - Listens for incoming TCP connections
- `Socket` - Client-side TCP connection
- `BufferedReader` / `PrintWriter` - Text stream I/O

**Characteristics:**
- Connection-oriented (3-way handshake)
- Reliable delivery (acknowledgments)
- Ordered packets
- Error checking and retransmission
- Best for: Chat, file transfer, HTTP

**Code Example:**
```java
ServerSocket serverSocket = new ServerSocket(5004);
Socket clientSocket = serverSocket.accept();
BufferedReader in = new BufferedReader(
    new InputStreamReader(clientSocket.getInputStream())
);
PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
```

### 2. Multithreading

**Implementation:** `ChatServer.java` with `ExecutorService`, `ClientHandler.java` implements `Runnable`

**Key APIs:**
- `ExecutorService` - Thread pool management
- `Runnable` - Task interface
- `synchronized` - Thread-safe operations
- `Thread` - Background execution

**Characteristics:**
- Concurrent client handling
- Thread pool prevents resource exhaustion
- Synchronized blocks for shared data
- Prevents blocking main server loop
- Best for: Scalable servers, concurrent tasks

**Code Example:**
```java
ExecutorService pool = Executors.newFixedThreadPool(10);
pool.submit(new ClientHandler(socket, this));

public synchronized void broadcast(String message, ClientHandler sender) {
    // Thread-safe message distribution
}
```

### 3. UDP Communication

**Implementation:** `UdpHealthServer.java`, `UdpHealthService.java`

**Key APIs:**
- `DatagramSocket` - UDP socket
- `DatagramPacket` - Individual UDP packets
- `InetAddress` - Network addressing

**Characteristics:**
- Connectionless (no handshake)
- No delivery guarantee
- No ordering guarantee
- Lower overhead than TCP
- Best for: Real-time data, DNS, streaming

**Code Example:**
```java
DatagramSocket socket = new DatagramSocket(5002);
byte[] buffer = new byte[1024];
DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
socket.receive(packet);

// Send response
byte[] responseData = response.getBytes();
DatagramPacket responsePacket = new DatagramPacket(
    responseData, responseData.length,
    packet.getAddress(), packet.getPort()
);
socket.send(responsePacket);
```

### 4. NIO (Non-blocking I/O)

**Implementation:** `NioFileServer.java`, `NioFileService.java`

**Key APIs:**
- `Selector` - Multiplexes multiple channels
- `ServerSocketChannel` - Non-blocking server socket
- `SocketChannel` - Non-blocking socket channel
- `ByteBuffer` - Efficient byte operations
- `SelectionKey` - Channel registration

**Characteristics:**
- Non-blocking operations
- Single thread handles multiple connections
- Selector pattern for I/O multiplexing
- Buffer-oriented (vs stream-oriented)
- Best for: High-concurrency servers, chat systems

**Code Example:**
```java
Selector selector = Selector.open();
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.configureBlocking(false);
serverChannel.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
    selector.select();
    Set<SelectionKey> keys = selector.selectedKeys();
    for (SelectionKey key : keys) {
        if (key.isAcceptable()) handleAccept(key);
        if (key.isReadable()) handleRead(key);
    }
}
```

### 5. URL and HTTP

**Implementation:** `LinkChecker.java`, `LinkCheckerService.java`

**Key APIs:**
- `URL` - URL parsing and validation
- `HttpURLConnection` - HTTP operations
- HTTP methods (HEAD, GET)
- Status codes (200, 404, 500, etc.)

**Characteristics:**
- Application-layer protocol
- Request-response model
- Status codes indicate result
- Headers provide metadata
- Best for: Web APIs, link validation, REST

**Code Example:**
```java
URL url = new URL(urlString);
HttpURLConnection connection = (HttpURLConnection) url.openConnection();
connection.setRequestMethod("HEAD");
connection.setConnectTimeout(5000);

int statusCode = connection.getResponseCode();
// 200 = OK, 404 = Not Found, 500 = Server Error
```

## 👥 Team Members

| Member | Responsibility | Implementation |
|--------|---------------|----------------|
| **Member 1** | TCP Server | ChatServer.java with ServerSocket, accept loop |
| **Member 2** | Multithreading | ExecutorService, ClientHandler (Runnable), synchronized |
| **Member 3** | UDP Server | UdpHealthServer with DatagramSocket |
| **Member 4** | NIO Server | NioFileServer with Selector pattern |
| **Member 5** | Client + HTTP | JavaFX GUI, LinkChecker with HttpURLConnection |

## 📊 Project Statistics

### Codebase Metrics
- **Total Java Files:** 16
- **Lines of Code:** ~2,800
- **Documentation:** ~1,200 lines
- **Services:** 3 concurrent servers
- **Ports:** 5004 (TCP), 5001 (NIO), 5002 (UDP)

### Backend
- **Files:** 8 Java files
- **Lines:** ~1,900
- **Features:** Thread pooling, NIO selector, UDP processing

### Frontend
- **Files:** 8 Java files + 1 FXML
- **Lines:** ~900
- **Features:** 4-tab UI, real-time updates, error handling

## 🐛 Troubleshooting

### Port Already in Use

**Error:** `Address already in use`

**Solution:**
```bash
# Find process using port
lsof -i :5004  # or 5001, 5002

# Kill process
kill -9 <PID>
```

### Connection Refused

**Error:** `Connection refused`

**Cause:** Backend servers not running

**Solution:** Ensure `MainServer` is running before starting client

### JavaFX Module Errors

**Error:** `Module not found`

**Solution:** 
- Verify Java 17 is installed: `java -version`
- Check `module-info.java` exports
- Ensure JavaFX dependencies in `pom.xml`

### File Not Found (NIO)

**Error:** `File 'filename' not found`

**Solution:**
- Place files in `backend/server_files/` directory
- Use exact filename (case-sensitive)
- Check file permissions

### Client UI Not Updating

**Cause:** Network operations blocking JavaFX thread

**Solution:** Already handled - all network ops run in background threads with `Platform.runLater()`

## 📚 Documentation

Additional documentation available:

- **PROJECT_SUMMARY.md** - Comprehensive technical overview
- **QUICKSTART.md** - Quick start guide for beginners
- **CLIENT_README.md** - Client-specific documentation
- **TCP_MULTITHREADING_README.md** - TCP and threading details
- **COMPLETE_IMPLEMENTATION_README.md** - UDP and NIO details
- **LINKCHECKER_README.md** - Link checker utility docs

## 🎓 Learning Outcomes

This project demonstrates:

✅ TCP/IP socket programming fundamentals  
✅ Concurrent programming with thread pools  
✅ Connectionless UDP communication  
✅ Non-blocking I/O with Java NIO  
✅ HTTP protocol operations  
✅ Client-server architecture  
✅ JavaFX GUI development  
✅ Service layer pattern  
✅ Thread-safe programming  
✅ Error handling and validation  

## 🔮 Future Enhancements

- [ ] SSL/TLS encryption for secure communication
- [ ] File upload to NIO server
- [ ] Private messaging in TCP chat
- [ ] Database integration for message persistence
- [ ] WebSocket support for real-time updates
- [ ] REST API integration
- [ ] Docker containerization
- [ ] Unit and integration tests
- [ ] Load balancing for multiple servers
- [ ] Configuration files for server settings

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **IN3111 Course Instructors** - For project guidance
- **Java Documentation** - Comprehensive API references
- **JavaFX Community** - UI framework support
- **Maven Central** - Dependency management

## 📧 Contact

For questions or issues:
- **Repository:** [Simple-Service-Hub](https://github.com/JalinaH/Simple-Service-Hub)
- **Branch:** dev-jalina
- **Course:** IN3111 - Network Programming

---

**Built with ❤️ using Java, JavaFX, and Maven**

*A demonstration of modern network programming concepts and best practices*
