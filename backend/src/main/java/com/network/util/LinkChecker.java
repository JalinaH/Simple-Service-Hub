package com.network.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * LinkChecker Utility - Validates URLs using HttpURLConnection
 * 
 * This utility demonstrates Java's higher-level networking classes:
 * - URL: Represents a Uniform Resource Locator
 * - URLConnection: Abstract class for URL connections
 * - HttpURLConnection: HTTP-specific URL connection (subclass of URLConnection)
 * 
 * Use Cases:
 * - Web scraping validation
 * - Link verification in websites
 * - Health monitoring of web services
 * - Dead link detection
 * - Sitemap validation
 * 
 * HTTP Response Codes:
 * - 200-299: Success (OK, Created, Accepted, etc.)
 * - 300-399: Redirection (Moved Permanently, Found, etc.)
 * - 400-499: Client errors (Bad Request, Not Found, Forbidden, etc.)
 * - 500-599: Server errors (Internal Server Error, Service Unavailable, etc.)
 */
public class LinkChecker {

    // Timeout settings (in milliseconds)
    private static final int CONNECTION_TIMEOUT = 5000;  // 5 seconds
    private static final int READ_TIMEOUT = 5000;        // 5 seconds
    
    // User agent to identify our requests
    private static final String USER_AGENT = "Mozilla/5.0 (LinkChecker/1.0)";

    /**
     * Result object containing link check information
     */
    public static class LinkCheckResult {
        private final String url;
        private final boolean isValid;
        private final int responseCode;
        private final String responseMessage;
        private final String errorMessage;
        private final long responseTime;  // in milliseconds

        public LinkCheckResult(String url, boolean isValid, int responseCode, 
                              String responseMessage, String errorMessage, long responseTime) {
            this.url = url;
            this.isValid = isValid;
            this.responseCode = responseCode;
            this.responseMessage = responseMessage;
            this.errorMessage = errorMessage;
            this.responseTime = responseTime;
        }

        public String getUrl() {
            return url;
        }

        public boolean isValid() {
            return isValid;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public long getResponseTime() {
            return responseTime;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("URL: ").append(url).append("\n");
            sb.append("Valid: ").append(isValid ? "✓ YES" : "✗ NO").append("\n");
            if (responseCode > 0) {
                sb.append("HTTP Code: ").append(responseCode).append(" (").append(responseMessage).append(")\n");
            }
            if (errorMessage != null) {
                sb.append("Error: ").append(errorMessage).append("\n");
            }
            sb.append("Response Time: ").append(responseTime).append("ms");
            return sb.toString();
        }
    }

    /**
     * Check if a URL is valid by making an HTTP request
     * 
     * This is the CORE method that:
     * 1. Creates a URL object from the string
     * 2. Opens an HttpURLConnection
     * 3. Checks the HTTP response code
     * 4. Handles all exceptions appropriately
     * 
     * @param urlString The URL to check (e.g., "https://www.example.com")
     * @return LinkCheckResult containing validation details
     */
    public static LinkCheckResult checkLink(String urlString) {
        long startTime = System.currentTimeMillis();
        
        // Validate input
        if (urlString == null || urlString.trim().isEmpty()) {
            return new LinkCheckResult(
                urlString,
                false,
                -1,
                null,
                "URL string is null or empty",
                0
            );
        }

        HttpURLConnection connection = null;
        
        try {
            // Step 1: Create a URL object
            // This parses the URL string and validates its format
            // Throws MalformedURLException if the URL is invalid
            URL url = new URL(urlString);
            
            // Step 2: Open an HttpURLConnection
            // url.openConnection() returns a URLConnection
            // We cast it to HttpURLConnection for HTTP-specific methods
            connection = (HttpURLConnection) url.openConnection();
            
            // Step 3: Configure the connection
            // Set request method (HEAD is faster than GET - only fetches headers)
            connection.setRequestMethod("HEAD");
            
            // Set timeouts to prevent hanging
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            
            // Set User-Agent header (some servers require this)
            connection.setRequestProperty("User-Agent", USER_AGENT);
            
            // Don't automatically follow redirects (we want to know about them)
            connection.setInstanceFollowRedirects(false);
            
            // Step 4: Connect and get response
            // This actually establishes the connection
            connection.connect();
            
            // Step 5: Get the HTTP response code
            // HttpURLConnection provides constants for common codes:
            // - HTTP_OK (200)
            // - HTTP_CREATED (201)
            // - HTTP_MOVED_PERM (301)
            // - HTTP_NOT_FOUND (404)
            // - HTTP_INTERNAL_ERROR (500)
            // etc.
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            // Step 6: Determine if link is valid based on response code
            // 2xx codes indicate success
            // 3xx codes indicate redirection (can be considered valid or not)
            boolean isValid = isSuccessfulResponse(responseCode);
            
            return new LinkCheckResult(
                urlString,
                isValid,
                responseCode,
                responseMessage,
                isValid ? null : "HTTP error: " + responseCode,
                responseTime
            );
            
        } catch (MalformedURLException e) {
            // The URL string is not a valid URL format
            // Examples: "not-a-url", "ht!tp://bad", "ftp://example.com:abc"
            long endTime = System.currentTimeMillis();
            return new LinkCheckResult(
                urlString,
                false,
                -1,
                null,
                "Malformed URL: " + e.getMessage(),
                endTime - startTime
            );
            
        } catch (UnknownHostException e) {
            // DNS lookup failed - host doesn't exist
            // Example: "http://this-domain-definitely-does-not-exist-12345.com"
            long endTime = System.currentTimeMillis();
            return new LinkCheckResult(
                urlString,
                false,
                -1,
                null,
                "Unknown host (DNS failed): " + e.getMessage(),
                endTime - startTime
            );
            
        } catch (IOException e) {
            // General I/O error (connection timeout, network error, etc.)
            // This catches all other IOException subtypes
            long endTime = System.currentTimeMillis();
            return new LinkCheckResult(
                urlString,
                false,
                -1,
                null,
                "I/O error: " + e.getMessage(),
                endTime - startTime
            );
            
        } finally {
            // Step 7: Clean up - always disconnect
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Determine if an HTTP response code indicates success
     * 
     * @param responseCode The HTTP response code
     * @return true if the code indicates success, false otherwise
     */
    private static boolean isSuccessfulResponse(int responseCode) {
        // 2xx codes are successful
        // 200 OK, 201 Created, 202 Accepted, 204 No Content, etc.
        if (responseCode >= 200 && responseCode < 300) {
            return true;
        }
        
        // 3xx codes are redirects - consider them valid but note them
        // (Some applications might want to treat these differently)
        // 4xx and 5xx are errors
        return responseCode >= 300 && responseCode < 400;
    }

    /**
     * Check multiple links and return results for all
     * 
     * @param urls List of URL strings to check
     * @return List of LinkCheckResult objects
     */
    public static List<LinkCheckResult> checkLinks(List<String> urls) {
        List<LinkCheckResult> results = new ArrayList<>();
        
        for (String url : urls) {
            results.add(checkLink(url));
        }
        
        return results;
    }

    /**
     * Quick validation - just returns true/false
     * 
     * @param urlString The URL to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidLink(String urlString) {
        return checkLink(urlString).isValid();
    }

    /**
     * Get HTTP response code for a URL
     * 
     * @param urlString The URL to check
     * @return HTTP response code, or -1 if error
     */
    public static int getResponseCode(String urlString) {
        return checkLink(urlString).getResponseCode();
    }

    /**
     * Get a human-readable status description for an HTTP code
     * 
     * @param code The HTTP response code
     * @return Description of the code
     */
    public static String getStatusDescription(int code) {
        switch (code) {
            // 2xx Success
            case HttpURLConnection.HTTP_OK:
                return "200 OK - The request succeeded";
            case HttpURLConnection.HTTP_CREATED:
                return "201 Created - Resource created successfully";
            case HttpURLConnection.HTTP_ACCEPTED:
                return "202 Accepted - Request accepted for processing";
            case HttpURLConnection.HTTP_NO_CONTENT:
                return "204 No Content - Success but no content to return";
            
            // 3xx Redirection
            case HttpURLConnection.HTTP_MOVED_PERM:
                return "301 Moved Permanently - Resource permanently moved";
            case HttpURLConnection.HTTP_MOVED_TEMP:
                return "302 Found - Resource temporarily moved";
            case HttpURLConnection.HTTP_SEE_OTHER:
                return "303 See Other - See another resource";
            case HttpURLConnection.HTTP_NOT_MODIFIED:
                return "304 Not Modified - Resource not modified";
            
            // 4xx Client Errors
            case HttpURLConnection.HTTP_BAD_REQUEST:
                return "400 Bad Request - Invalid request syntax";
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                return "401 Unauthorized - Authentication required";
            case HttpURLConnection.HTTP_FORBIDDEN:
                return "403 Forbidden - Access denied";
            case HttpURLConnection.HTTP_NOT_FOUND:
                return "404 Not Found - Resource not found";
            case HttpURLConnection.HTTP_BAD_METHOD:
                return "405 Method Not Allowed - HTTP method not supported";
            case HttpURLConnection.HTTP_CONFLICT:
                return "409 Conflict - Request conflicts with current state";
            
            // 5xx Server Errors
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                return "500 Internal Server Error - Server error";
            case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
                return "501 Not Implemented - Server doesn't support functionality";
            case HttpURLConnection.HTTP_BAD_GATEWAY:
                return "502 Bad Gateway - Invalid response from upstream server";
            case HttpURLConnection.HTTP_UNAVAILABLE:
                return "503 Service Unavailable - Server temporarily unavailable";
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                return "504 Gateway Timeout - Upstream server timeout";
            
            default:
                if (code >= 200 && code < 300) {
                    return code + " Success";
                } else if (code >= 300 && code < 400) {
                    return code + " Redirection";
                } else if (code >= 400 && code < 500) {
                    return code + " Client Error";
                } else if (code >= 500 && code < 600) {
                    return code + " Server Error";
                } else {
                    return code + " Unknown";
                }
        }
    }

    /**
     * Main method for standalone testing and demonstration
     */
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("           LINK CHECKER UTILITY - DEMONSTRATION");
        System.out.println("============================================================");
        System.out.println();

        // Test URLs covering various scenarios
        List<String> testUrls = new ArrayList<>();
        testUrls.add("https://www.google.com");              // Valid
        testUrls.add("https://www.github.com");              // Valid
        testUrls.add("http://httpstat.us/200");              // Valid - Test endpoint
        testUrls.add("http://httpstat.us/404");              // Not Found
        testUrls.add("http://httpstat.us/500");              // Server Error
        testUrls.add("https://this-domain-does-not-exist-12345.com");  // DNS fail
        testUrls.add("not-a-valid-url");                     // Malformed
        testUrls.add("htp://missing-t.com");                 // Malformed
        testUrls.add("https://www.stackoverflow.com");       // Valid

        System.out.println("Testing " + testUrls.size() + " URLs...");
        System.out.println();

        int validCount = 0;
        int invalidCount = 0;

        for (String url : testUrls) {
            System.out.println("Testing: " + url);
            System.out.println("------------------------------------------------------------");
            
            LinkCheckResult result = checkLink(url);
            System.out.println(result);
            
            if (result.isValid()) {
                validCount++;
            } else {
                invalidCount++;
            }
            
            System.out.println("------------------------------------------------------------");
            System.out.println();
        }

        System.out.println("============================================================");
        System.out.println("SUMMARY");
        System.out.println("============================================================");
        System.out.println("Total URLs tested: " + testUrls.size());
        System.out.println("Valid links: " + validCount);
        System.out.println("Invalid links: " + invalidCount);
        System.out.println("============================================================");
        
        // Demonstrate HTTP response code descriptions
        System.out.println();
        System.out.println("HTTP Response Code Reference:");
        System.out.println("------------------------------------------------------------");
        int[] commonCodes = {200, 301, 302, 400, 401, 403, 404, 500, 502, 503};
        for (int code : commonCodes) {
            System.out.println(getStatusDescription(code));
        }
        System.out.println("============================================================");
    }
}
