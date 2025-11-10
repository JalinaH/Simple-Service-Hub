package com.network.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.network.util.LinkChecker.LinkCheckResult;

/**
 * Example Client demonstrating how to use the LinkChecker utility
 * 
 * This shows various ways to use the LinkChecker class:
 * - Single link validation
 * - Batch link checking
 * - Quick validation
 * - Interactive link checker
 */
public class LinkCheckerClient {

    /**
     * Helper method to repeat a string (Java 8 compatible)
     */
    private static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        printBanner();
        
        if (args.length > 0) {
            // Command-line mode: check URLs passed as arguments
            System.out.println("Command-line mode: Checking provided URLs");
            System.out.println();
            
            for (String url : args) {
                checkAndDisplayLink(url);
            }
        } else {
            // Interactive mode
            runInteractiveMode();
        }
    }

    /**
     * Run interactive mode where user can enter URLs
     */
    private static void runInteractiveMode() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Interactive Link Checker");
            System.out.println("========================");
            System.out.println();
            System.out.println("Commands:");
            System.out.println("  <URL>         - Check a single URL");
            System.out.println("  batch         - Enter multiple URLs to check");
            System.out.println("  examples      - Check example URLs");
            System.out.println("  quick <URL>   - Quick check (just true/false)");
            System.out.println("  code <URL>    - Get HTTP response code only");
            System.out.println("  quit          - Exit");
            System.out.println();

            boolean running = true;
            while (running) {
                System.out.print("Enter command or URL: ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                    running = false;
                    System.out.println("Goodbye!");
                } else if (input.equalsIgnoreCase("batch")) {
                    batchCheck(scanner);
                } else if (input.equalsIgnoreCase("examples")) {
                    checkExamples();
                } else if (input.toLowerCase().startsWith("quick ")) {
                    String url = input.substring(6).trim();
                    quickCheck(url);
                } else if (input.toLowerCase().startsWith("code ")) {
                    String url = input.substring(5).trim();
                    getCodeOnly(url);
                } else {
                    // Assume it's a URL
                    checkAndDisplayLink(input);
                }
                
                System.out.println();
            }
        }
    }

    /**
     * Check a single link and display detailed results
     */
    private static void checkAndDisplayLink(String url) {
        System.out.println("Checking: " + url);
        System.out.println(repeat("-", 60));
        
        LinkCheckResult result = LinkChecker.checkLink(url);
        System.out.println(result);
        
        if (result.getResponseCode() > 0) {
            System.out.println("Description: " + 
                LinkChecker.getStatusDescription(result.getResponseCode()));
        }
        
        System.out.println(repeat("-", 60));
    }

    /**
     * Quick check - just shows if URL is valid
     */
    private static void quickCheck(String url) {
        System.out.println("Quick check: " + url);
        boolean isValid = LinkChecker.isValidLink(url);
        System.out.println("Result: " + (isValid ? "✓ VALID" : "✗ INVALID"));
    }

    /**
     * Get only the HTTP response code
     */
    private static void getCodeOnly(String url) {
        System.out.println("Getting response code for: " + url);
        int code = LinkChecker.getResponseCode(url);
        if (code > 0) {
            System.out.println("Response Code: " + code);
            System.out.println(LinkChecker.getStatusDescription(code));
        } else {
            System.out.println("Could not get response code (connection error)");
        }
    }

    /**
     * Check multiple URLs in batch
     */
    private static void batchCheck(Scanner scanner) {
        System.out.println();
        System.out.println("Batch Mode - Enter URLs (one per line)");
        System.out.println("Type 'done' when finished");
        System.out.println();
        
        List<String> urls = new ArrayList<>();
        
        while (true) {
            System.out.print("URL " + (urls.size() + 1) + ": ");
            String url = scanner.nextLine().trim();
            
            if (url.equalsIgnoreCase("done")) {
                break;
            }
            
            if (!url.isEmpty()) {
                urls.add(url);
            }
        }
        
        if (urls.isEmpty()) {
            System.out.println("No URLs entered.");
            return;
        }
        
        System.out.println();
        System.out.println("Checking " + urls.size() + " URLs...");
        System.out.println(repeat("=", 60));
        
        List<LinkCheckResult> results = LinkChecker.checkLinks(urls);
        
        int validCount = 0;
        int invalidCount = 0;
        
        for (LinkCheckResult result : results) {
            System.out.println();
            System.out.println("URL: " + result.getUrl());
            System.out.println("Status: " + (result.isValid() ? "✓ VALID" : "✗ INVALID"));
            if (result.getResponseCode() > 0) {
                System.out.println("Code: " + result.getResponseCode() + " - " + 
                    result.getResponseMessage());
            }
            if (result.getErrorMessage() != null) {
                System.out.println("Error: " + result.getErrorMessage());
            }
            System.out.println("Time: " + result.getResponseTime() + "ms");
            System.out.println(repeat("-", 60));
            
            if (result.isValid()) {
                validCount++;
            } else {
                invalidCount++;
            }
        }
        
        System.out.println();
        System.out.println("Summary: " + validCount + " valid, " + invalidCount + " invalid");
    }

    /**
     * Check some example URLs to demonstrate functionality
     */
    private static void checkExamples() {
        System.out.println();
        System.out.println("Checking Example URLs");
        System.out.println(repeat("=", 60));
        System.out.println();
        
        List<String> examples = new ArrayList<>();
        examples.add("https://www.google.com");
        examples.add("https://www.github.com");
        examples.add("https://example.com");
        examples.add("http://localhost:5000");  // Local server
        examples.add("https://invalid-domain-12345.com");  // Should fail
        
        for (String url : examples) {
            checkAndDisplayLink(url);
            System.out.println();
        }
    }

    /**
     * Print banner
     */
    private static void printBanner() {
        System.out.println("============================================================");
        System.out.println("           LINK CHECKER CLIENT - EXAMPLE USAGE");
        System.out.println("============================================================");
        System.out.println();
    }
}
