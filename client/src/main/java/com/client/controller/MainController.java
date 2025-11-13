package com.client.controller;

import java.io.File;
import java.io.IOException;

import com.client.service.LinkCheckerService;
import com.client.service.LinkCheckerService.LinkCheckResult;
import com.client.service.NioFileService;
import com.client.service.TcpChatService;
import com.client.service.UdpHealthService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * Main Controller for NetHub Client
 * Manages all tabs and their respective network services
 */
public class MainController {
    
    // TCP Chat Tab
    @FXML private TextField tcpUsernameField;
    @FXML private Button tcpConnectBtn;
    @FXML private TextArea tcpChatArea;
    @FXML private TextField tcpMessageField;
    @FXML private Button tcpSendBtn;
    @FXML private Label tcpStatusLabel;
    
    // UDP Health Tab
    @FXML private Button udpInitBtn;
    @FXML private TextArea udpResponseArea;
    @FXML private Button udpPingBtn;
    @FXML private Button udpStatusBtn;
    @FXML private Button udpInfoBtn;
    @FXML private Button udpTimeBtn;
    @FXML private Button udpUptimeBtn;
    @FXML private TextField udpCommandField;
    @FXML private Button udpSendBtn;
    @FXML private Label udpStatusLabel;
    
    // NIO File Tab
    @FXML private Button nioConnectBtn;
    @FXML private TextArea nioFileListArea;
    @FXML private Button nioListBtn;
    @FXML private TextField nioFilenameField;
    @FXML private Button nioDownloadBtn;
    @FXML private Button nioUploadBtn;
    @FXML private Label nioStatusLabel;
    
    // Link Checker Tab
    @FXML private TextField linkUrlField;
    @FXML private Button linkCheckBtn;
    @FXML private TextArea linkResultArea;
    
    // Services
    private TcpChatService tcpService;
    private UdpHealthService udpService;
    private NioFileService nioService;
    private LinkCheckerService linkService;
    
    @FXML
    public void initialize() {
        // Initialize services
        tcpService = new TcpChatService();
        udpService = new UdpHealthService();
        nioService = new NioFileService();
        linkService = new LinkCheckerService();
        
        // Setup initial button states
        setupTcpTab();
        setupUdpTab();
        setupNioTab();
        setupLinkCheckerTab();
    }
    
    // ==================== TCP CHAT TAB ====================
    
    private void setupTcpTab() {
        tcpSendBtn.setDisable(true);
        tcpMessageField.setDisable(true);
        
        tcpConnectBtn.setOnAction(e -> handleTcpConnect());
        tcpSendBtn.setOnAction(e -> handleTcpSend());
        tcpMessageField.setOnAction(e -> handleTcpSend());
    }
    
    private void handleTcpConnect() {
        String username = tcpUsernameField.getText().trim();
        
        if (tcpService.isConnected()) {
            // Disconnect
            tcpService.disconnect();
            tcpConnectBtn.setText("Connect");
            tcpSendBtn.setDisable(true);
            tcpMessageField.setDisable(true);
            tcpUsernameField.setDisable(false);
            return;
        }
        
        if (username.isEmpty()) {
            showAlert("Error", "Please enter a username");
            return;
        }
        
        try {
            tcpService.connect(
                username,
                message -> tcpChatArea.appendText(message + "\n"),
                status -> tcpStatusLabel.setText("Status: " + status)
            );
            
            tcpConnectBtn.setText("Disconnect");
            tcpSendBtn.setDisable(false);
            tcpMessageField.setDisable(false);
            tcpUsernameField.setDisable(true);
            tcpChatArea.appendText("=== Connected to TCP Chat Server ===\n");
            
        } catch (IOException ex) {
            showAlert("Connection Error", "Failed to connect: " + ex.getMessage());
            tcpStatusLabel.setText("Status: Connection failed");
        }
    }
    
    private void handleTcpSend() {
        String message = tcpMessageField.getText().trim();
        if (!message.isEmpty() && tcpService.isConnected()) {
            tcpService.sendMessage(message);
            tcpMessageField.clear();
        }
    }
    
    // ==================== UDP HEALTH TAB ====================
    
    private void setupUdpTab() {
        udpPingBtn.setDisable(true);
        udpStatusBtn.setDisable(true);
        udpInfoBtn.setDisable(true);
        udpTimeBtn.setDisable(true);
        udpUptimeBtn.setDisable(true);
        udpCommandField.setDisable(true);
        udpSendBtn.setDisable(true);
        
        udpInitBtn.setOnAction(e -> handleUdpInit());
        udpPingBtn.setOnAction(e -> handleUdpCommand("PING"));
        udpStatusBtn.setOnAction(e -> handleUdpCommand("STATUS"));
        udpInfoBtn.setOnAction(e -> handleUdpCommand("INFO"));
        udpTimeBtn.setOnAction(e -> handleUdpCommand("TIME"));
        udpUptimeBtn.setOnAction(e -> handleUdpCommand("UPTIME"));
        udpSendBtn.setOnAction(e -> handleUdpCustomCommand());
        udpCommandField.setOnAction(e -> handleUdpCustomCommand());
    }
    
    private void handleUdpInit() {
        if (udpService.isActive()) {
            // Close
            udpService.close();
            udpInitBtn.setText("Initialize");
            udpPingBtn.setDisable(true);
            udpStatusBtn.setDisable(true);
            udpInfoBtn.setDisable(true);
            udpTimeBtn.setDisable(true);
            udpUptimeBtn.setDisable(true);
            udpCommandField.setDisable(true);
            udpSendBtn.setDisable(true);
            udpStatusLabel.setText("Status: Closed");
            return;
        }
        
        try {
            udpService.initialize();
            udpInitBtn.setText("Close");
            udpPingBtn.setDisable(false);
            udpStatusBtn.setDisable(false);
            udpInfoBtn.setDisable(false);
            udpTimeBtn.setDisable(false);
            udpUptimeBtn.setDisable(false);
            udpCommandField.setDisable(false);
            udpSendBtn.setDisable(false);
            udpStatusLabel.setText("Status: Initialized");
            udpResponseArea.appendText("=== UDP Service Initialized ===\n");
            udpResponseArea.appendText("Type 'HELP' in command field for available commands\n\n");
        } catch (IOException ex) {
            showAlert("Error", "Failed to initialize UDP service: " + ex.getMessage());
            udpStatusLabel.setText("Status: Initialization failed");
        }
    }
    
    private void handleUdpCustomCommand() {
        String command = udpCommandField.getText().trim();
        if (!command.isEmpty() && udpService.isActive()) {
            handleUdpCommand(command);
            udpCommandField.clear();
        }
    }
    
    private void handleUdpCommand(String command) {
        if (!udpService.isActive()) {
            return;
        }
        
        new Thread(() -> {
            try {
                String response = udpService.sendCommand(command);
                Platform.runLater(() -> {
                    udpResponseArea.appendText("\n>>> " + command + "\n");
                    udpResponseArea.appendText(response + "\n");
                    udpStatusLabel.setText("Status: Command sent successfully");
                });
            } catch (IOException ex) {
                Platform.runLater(() -> {
                    udpResponseArea.appendText("\n>>> " + command + "\n");
                    udpResponseArea.appendText("ERROR: " + ex.getMessage() + "\n");
                    udpStatusLabel.setText("Status: Command failed");
                });
            }
        }).start();
    }
    
    // ==================== NIO FILE TAB ====================
    
    private void setupNioTab() {
        nioListBtn.setDisable(true);
        nioDownloadBtn.setDisable(true);
        nioUploadBtn.setDisable(true);
        nioFilenameField.setDisable(true);
        
        nioConnectBtn.setOnAction(e -> handleNioConnect());
        nioListBtn.setOnAction(e -> handleNioList());
        nioDownloadBtn.setOnAction(e -> handleNioDownload());
        nioUploadBtn.setOnAction(e -> handleNioUpload());
    }
    
    private void handleNioConnect() {
        if (nioService.isConnected()) {
            // Disconnect
            nioService.disconnect();
            nioConnectBtn.setText("Connect");
            nioListBtn.setDisable(true);
            nioDownloadBtn.setDisable(true);
            nioUploadBtn.setDisable(true);
            nioFilenameField.setDisable(true);
            nioStatusLabel.setText("Status: Disconnected");
            return;
        }
        
        try {
            nioService.connect();
            nioConnectBtn.setText("Disconnect");
            nioListBtn.setDisable(false);
            nioDownloadBtn.setDisable(false);
            nioUploadBtn.setDisable(false);
            nioFilenameField.setDisable(false);
            nioStatusLabel.setText("Status: Connected to NIO File Server");
            nioFileListArea.appendText("=== Connected to NIO File Server ===\n");
        } catch (IOException ex) {
            showAlert("Connection Error", "Failed to connect: " + ex.getMessage());
            nioStatusLabel.setText("Status: Connection failed");
        }
    }
    
    private void handleNioList() {
        if (!nioService.isConnected()) {
            return;
        }
        
        new Thread(() -> {
            try {
                String fileList = nioService.listFiles();
                Platform.runLater(() -> {
                    nioFileListArea.appendText("\n=== Available Files ===\n");
                    nioFileListArea.appendText(fileList);
                    nioStatusLabel.setText("Status: File list retrieved");
                });
            } catch (IOException ex) {
                Platform.runLater(() -> {
                    nioFileListArea.appendText("\nERROR: " + ex.getMessage() + "\n");
                    nioStatusLabel.setText("Status: Failed to retrieve file list");
                });
            }
        }).start();
    }
    
    private void handleNioDownload() {
        String filename = nioFilenameField.getText().trim();
        if (filename.isEmpty()) {
            showAlert("Error", "Please enter a filename");
            return;
        }
        
        if (!nioService.isConnected()) {
            showAlert("Error", "Not connected to server");
            return;
        }
        
        // Choose download directory
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Download Directory");
        File directory = chooser.showDialog(nioDownloadBtn.getScene().getWindow());
        
        if (directory == null) {
            return;
        }
        
        new Thread(() -> {
            try {
                String savedPath = nioService.downloadFile(filename, directory.getAbsolutePath());
                Platform.runLater(() -> {
                    nioFileListArea.appendText("\n✓ Downloaded: " + savedPath + "\n");
                    nioStatusLabel.setText("Status: File downloaded successfully");
                    nioFilenameField.clear();
                });
            } catch (IOException ex) {
                Platform.runLater(() -> {
                    nioFileListArea.appendText("\n✗ Download failed: " + ex.getMessage() + "\n");
                    nioStatusLabel.setText("Status: Download failed");
                });
            }
        }).start();
    }
    
    private void handleNioUpload() {
        if (!nioService.isConnected()) {
            showAlert("Error", "Not connected to server");
            return;
        }
        
        // Choose file to upload
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        File file = fileChooser.showOpenDialog(nioUploadBtn.getScene().getWindow());
        
        if (file == null) {
            return;
        }
        
        new Thread(() -> {
            try {
                String result = nioService.uploadFile(file.getAbsolutePath());
                Platform.runLater(() -> {
                    nioFileListArea.appendText("\n✓ " + result + "\n");
                    nioStatusLabel.setText("Status: File uploaded successfully");
                });
            } catch (IOException ex) {
                Platform.runLater(() -> {
                    nioFileListArea.appendText("\n✗ Upload failed: " + ex.getMessage() + "\n");
                    nioStatusLabel.setText("Status: Upload failed");
                });
            }
        }).start();
    }
    
    // ==================== LINK CHECKER TAB ====================
    
    private void setupLinkCheckerTab() {
        linkCheckBtn.setOnAction(e -> handleLinkCheck());
        linkUrlField.setOnAction(e -> handleLinkCheck());
    }
    
    private void handleLinkCheck() {
        String url = linkUrlField.getText().trim();
        if (url.isEmpty()) {
            showAlert("Error", "Please enter a URL");
            return;
        }
        
        linkCheckBtn.setDisable(true);
        linkResultArea.appendText("\nChecking: " + url + "\n");
        
        new Thread(() -> {
            LinkCheckResult result = linkService.checkLink(url);
            Platform.runLater(() -> {
                linkResultArea.appendText(result.toString() + "\n");
                linkCheckBtn.setDisable(false);
            });
        }).start();
    }
    
    // ==================== UTILITIES ====================
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
