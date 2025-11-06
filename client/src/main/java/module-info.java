module com.client.client {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.client.client to javafx.fxml;
    exports com.client.client;
}