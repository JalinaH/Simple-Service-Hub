package com;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LinkChecker {

    // Main function for testing
    public static void main(String[] args) {
        // Test some sample links
        checkLink("https://www.google.com");
        checkLink("https://www.facebook.com");
        checkLink("https://thiswebsitedoesnotexist.abc");
    }

    // Method that checks if a URL is valid
    public static void checkLink(String urlString) {
        try {
            // Create a URL object from URI
            URL url = java.net.URI.create(urlString).toURL();

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Get the response code
            int code = connection.getResponseCode();

            // Check response
            if (code == HttpURLConnection.HTTP_OK) {
                System.out.println(urlString + " is VALID ✅");
            } else {
                System.out.println(urlString + " returned code: " + code + " ⚠️");
            }

            // Disconnect after checking
            connection.disconnect();

        } catch (MalformedURLException e) {
            System.out.println("Invalid URL format ❌: " + urlString);
        } catch (IOException e) {
            System.out.println("Error connecting to URL ⚠️: " + urlString);
        }
    }
}
