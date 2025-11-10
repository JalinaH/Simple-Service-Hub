# LinkChecker Utility - URL & HttpURLConnection Implementation

## 📋 Overview

The **LinkChecker** utility demonstrates Java's higher-level networking classes for interacting with the web. It provides a simple, reusable way to validate URLs and check link status using `URL` and `HttpURLConnection`.

---

## ✅ Implementation Checklist

### **Core Requirements:**
- ✅ **URL object creation** from string
- ✅ **HttpURLConnection** using `url.openConnection()`
- ✅ **HTTP response code checking** (HTTP_OK, HTTP_NOT_FOUND, etc.)
- ✅ **Exception handling:**
  - MalformedURLException (invalid URL format)
  - UnknownHostException (DNS lookup failure)
  - IOException (general network errors)

### **Additional Features:**
- ✅ Detailed result object with response time
- ✅ Batch link checking
- ✅ HTTP status code descriptions
- ✅ Interactive client application
- ✅ Timeout configuration
- ✅ User-Agent header setting

---

## 🔑 Key Classes Demonstrated

### **1. URL Class**
```java
// Creates a URL object from a string
URL url = new URL("https://www.example.com");

// Components of a URL:
// - Protocol: https
// - Host: www.example.com
// - Port: (default 443 for https)
// - Path: /
```

**Purpose:**
- Represents a Uniform Resource Locator
- Parses and validates URL format
- Provides access to URL components
- Foundation for making HTTP requests

### **2. HttpURLConnection Class**
```java
// Open connection (subclass of URLConnection)
HttpURLConnection connection = (HttpURLConnection) url.openConnection();

// Configure the connection
connection.setRequestMethod("HEAD");  // or "GET", "POST", etc.
connection.setConnectTimeout(5000);    // 5 seconds
connection.setReadTimeout(5000);        // 5 seconds

// Make the request
connection.connect();

// Get response
int responseCode = connection.getResponseCode();
String responseMessage = connection.getResponseMessage();

// Always disconnect
connection.disconnect();
```

**Purpose:**
- HTTP-specific implementation of URLConnection
- Provides HTTP methods (GET, POST, HEAD, etc.)
- Access to HTTP response codes and headers
- Control over timeouts and redirects

### **3. Exception Handling**

```java
try {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.connect();
    int code = conn.getResponseCode();
    
} catch (MalformedURLException e) {
    // Invalid URL format
    // Examples: "not-a-url", "ht!tp://bad"
    
} catch (UnknownHostException e) {
    // DNS lookup failed - host doesn't exist
    // Example: "http://this-does-not-exist.com"
    
} catch (IOException e) {
    // General network error
    // Connection timeout, network unreachable, etc.
    
} finally {
    // Always cleanup
    if (conn != null) {
        conn.disconnect();
    }
}
```

---

## 🚀 Usage Examples

### **1. Basic Link Checking**

```java
import com.network.util.LinkChecker;
import com.network.util.LinkChecker.LinkCheckResult;

// Check a single link
LinkCheckResult result = LinkChecker.checkLink("https://www.google.com");

System.out.println("Valid: " + result.isValid());
System.out.println("Response Code: " + result.getResponseCode());
System.out.println("Response Time: " + result.getResponseTime() + "ms");
```

### **2. Quick Validation**

```java
// Just want true/false?
boolean isValid = LinkChecker.isValidLink("https://www.github.com");

if (isValid) {
    System.out.println("Link is accessible!");
} else {
    System.out.println("Link is broken or invalid!");
}
```

### **3. Get Response Code Only**

```java
int code = LinkChecker.getResponseCode("https://www.example.com");

if (code == 200) {
    System.out.println("Link returns OK");
} else if (code == 404) {
    System.out.println("Page not found");
} else if (code == -1) {
    System.out.println("Connection error");
}
```

### **4. Batch Checking**

```java
List<String> urls = Arrays.asList(
    "https://www.google.com",
    "https://www.github.com",
    "https://invalid-domain.com"
);

List<LinkCheckResult> results = LinkChecker.checkLinks(urls);

for (LinkCheckResult result : results) {
    System.out.println(result.getUrl() + " -> " + 
        (result.isValid() ? "✓" : "✗"));
}
```

### **5. Using the Interactive Client**

```java
// Run the client application
java -cp target/classes com.network.util.LinkCheckerClient

// Or with command-line arguments
java -cp target/classes com.network.util.LinkCheckerClient \
    https://www.google.com \
    https://www.github.com
```

---

## 📊 HTTP Response Codes

### **2xx Success**
- **200 OK** - Request succeeded
- **201 Created** - Resource created
- **202 Accepted** - Request accepted
- **204 No Content** - Success but no content

### **3xx Redirection**
- **301 Moved Permanently** - Resource moved permanently
- **302 Found** - Resource temporarily moved
- **303 See Other** - See another resource
- **304 Not Modified** - Resource not modified

### **4xx Client Errors**
- **400 Bad Request** - Invalid request syntax
- **401 Unauthorized** - Authentication required
- **403 Forbidden** - Access denied
- **404 Not Found** - Resource not found
- **405 Method Not Allowed** - HTTP method not supported

### **5xx Server Errors**
- **500 Internal Server Error** - Server error
- **502 Bad Gateway** - Invalid response from upstream
- **503 Service Unavailable** - Server temporarily unavailable
- **504 Gateway Timeout** - Upstream server timeout

---

## 🧪 Testing

### **Test the Standalone LinkChecker**
```bash
cd backend
mvn clean compile

# Run the demonstration
java -cp target/classes com.network.util.LinkChecker
```

### **Test the Interactive Client**
```bash
# Interactive mode
java -cp target/classes com.network.util.LinkCheckerClient

# Command-line mode
java -cp target/classes com.network.util.LinkCheckerClient \
    https://www.google.com \
    https://www.github.com \
    https://www.stackoverflow.com
```

### **Sample Output**
```
Testing: https://www.google.com
------------------------------------------------------------
URL: https://www.google.com
Valid: ✓ YES
HTTP Code: 200 (OK)
Response Time: 302ms
Description: 200 OK - The request succeeded
------------------------------------------------------------

Testing: https://invalid-domain-12345.com
------------------------------------------------------------
URL: https://invalid-domain-12345.com
Valid: ✗ NO
Error: Unknown host (DNS failed): invalid-domain-12345.com
Response Time: 405ms
------------------------------------------------------------

Testing: not-a-valid-url
------------------------------------------------------------
URL: not-a-valid-url
Valid: ✗ NO
Error: Malformed URL: no protocol: not-a-valid-url
Response Time: 0ms
------------------------------------------------------------
```

---

## 🔧 Configuration Options

### **Timeout Settings**
```java
// In LinkChecker.java, modify these constants:
private static final int CONNECTION_TIMEOUT = 5000;  // 5 seconds
private static final int READ_TIMEOUT = 5000;        // 5 seconds
```

### **Request Method**
```java
// Current: Uses HEAD (faster, only fetches headers)
connection.setRequestMethod("HEAD");

// Alternative: Use GET (fetches full content)
connection.setRequestMethod("GET");
```

### **Follow Redirects**
```java
// Current: Don't follow redirects automatically
connection.setInstanceFollowRedirects(false);

// Alternative: Follow redirects
connection.setInstanceFollowRedirects(true);
```

### **User Agent**
```java
// Identifies your application to the server
private static final String USER_AGENT = "Mozilla/5.0 (LinkChecker/1.0)";
```

---

## 💡 Use Cases

### **1. Website Monitoring**
```java
// Check if your website is up
List<String> criticalPages = Arrays.asList(
    "https://mysite.com",
    "https://mysite.com/api/health",
    "https://mysite.com/login"
);

for (String page : criticalPages) {
    LinkCheckResult result = LinkChecker.checkLink(page);
    if (!result.isValid()) {
        // Send alert!
        sendAlert("Page down: " + page);
    }
}
```

### **2. Broken Link Detection**
```java
// Scrape links from a webpage, then validate them
List<String> linksFromPage = scrapeLinks("https://mysite.com");
List<LinkCheckResult> results = LinkChecker.checkLinks(linksFromPage);

List<String> brokenLinks = results.stream()
    .filter(r -> !r.isValid())
    .map(LinkCheckResult::getUrl)
    .collect(Collectors.toList());

System.out.println("Found " + brokenLinks.size() + " broken links");
```

### **3. API Health Checks**
```java
// Check multiple API endpoints
String[] endpoints = {
    "https://api.myservice.com/health",
    "https://api.myservice.com/status",
    "https://api.myservice.com/metrics"
};

for (String endpoint : endpoints) {
    int code = LinkChecker.getResponseCode(endpoint);
    if (code == 200) {
        System.out.println("✓ " + endpoint + " is healthy");
    } else {
        System.out.println("✗ " + endpoint + " returned " + code);
    }
}
```

### **4. Performance Monitoring**
```java
// Track response times
LinkCheckResult result = LinkChecker.checkLink("https://api.myservice.com");
long responseTime = result.getResponseTime();

if (responseTime > 1000) {
    System.out.println("WARNING: Slow response (" + responseTime + "ms)");
}
```

---

## 🎯 Key Learning Outcomes

### **URL Class**
✅ Creating URL objects from strings  
✅ Understanding URL components  
✅ URL validation and parsing  
✅ Handling malformed URLs  

### **HttpURLConnection**
✅ Opening HTTP connections  
✅ Configuring request methods  
✅ Setting timeouts  
✅ Reading response codes and messages  
✅ Managing connection lifecycle  

### **Exception Handling**
✅ Catching MalformedURLException  
✅ Handling UnknownHostException  
✅ Managing general IOException  
✅ Proper resource cleanup  

### **HTTP Protocol**
✅ Understanding status codes  
✅ Success vs error responses  
✅ Redirects and their meaning  
✅ Best practices for web requests  

---

## 🔒 Best Practices Implemented

1. **Always Set Timeouts**
   - Prevents hanging indefinitely
   - Connection timeout for establishing connection
   - Read timeout for receiving data

2. **Always Disconnect**
   - Use try-finally or try-with-resources
   - Clean up connections properly
   - Prevent resource leaks

3. **Use Appropriate HTTP Method**
   - HEAD for link checking (faster)
   - GET when you need content
   - POST for submitting data

4. **Set User-Agent**
   - Some servers require it
   - Identifies your application
   - Helps with debugging

5. **Handle All Exception Types**
   - MalformedURLException: Invalid URL format
   - UnknownHostException: DNS issues
   - IOException: Network problems

---

## 📝 Integration with Other Services

### **Use in NIO File Server**
```java
// Validate URLs before downloading files
public void downloadFromURL(String urlString) {
    LinkCheckResult result = LinkChecker.checkLink(urlString);
    
    if (!result.isValid()) {
        throw new IOException("URL is not accessible: " + 
            result.getErrorMessage());
    }
    
    // Proceed with download...
}
```

### **Use in TCP Chat Server**
```java
// Check if shared links are valid
public void handleMessage(String message) {
    if (message.startsWith("http://") || message.startsWith("https://")) {
        boolean isValid = LinkChecker.isValidLink(message);
        String status = isValid ? "[✓ Valid Link]" : "[✗ Broken Link]";
        broadcast(message + " " + status);
    }
}
```

---

## 📊 Class Structure

```
LinkChecker.java
├── LinkCheckResult (inner class)
│   ├── url: String
│   ├── isValid: boolean
│   ├── responseCode: int
│   ├── responseMessage: String
│   ├── errorMessage: String
│   └── responseTime: long
│
├── checkLink(String): LinkCheckResult
│   └── Core method - validates URL, opens connection, checks code
│
├── checkLinks(List<String>): List<LinkCheckResult>
│   └── Batch checking
│
├── isValidLink(String): boolean
│   └── Quick validation
│
├── getResponseCode(String): int
│   └── Get code only
│
├── getStatusDescription(int): String
│   └── Human-readable status
│
└── main(String[]): void
    └── Demonstration and testing

LinkCheckerClient.java
├── Interactive mode
├── Command-line mode
├── Batch checking
└── Example usage
```

---

## 🚀 Running Examples

### **Example 1: Check Single URL**
```bash
java -cp target/classes com.network.util.LinkCheckerClient \
    https://www.google.com
```

### **Example 2: Check Multiple URLs**
```bash
java -cp target/classes com.network.util.LinkCheckerClient \
    https://www.google.com \
    https://www.github.com \
    https://www.stackoverflow.com
```

### **Example 3: Interactive Mode**
```bash
java -cp target/classes com.network.util.LinkCheckerClient

# Then type:
> https://www.google.com
> quick https://www.github.com
> code https://www.example.com
> batch
> quit
```

---

**Implementation Date:** November 10, 2025  
**Course:** Network Programming (L3S1)  
**Assignment:** Simple Service Hub - URL & HttpURLConnection Module  
**Status:** ✅ FULLY COMPLETE
