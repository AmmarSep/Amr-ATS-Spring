# HealthController.java - Detailed Line-by-Line Explanation

## Class Declaration (Lines 15-16)

```java
@RestController
public class HealthController {
```

- **`@RestController`** - Marks this as a REST Controller (returns data, not HTML)
- **`public class HealthController`** - A public class that checks if the application is healthy

**Purpose:** This controller provides a health check endpoint that tells monitoring systems if the application is running properly.

---

## Field Declaration (Lines 18-19)

```java
@Autowired(required = false)
private DataSource dataSource;
```

- **`@Autowired(required = false)`** - Spring tries to inject a DataSource, but it's optional
  - **`required = false`** means "If DataSource doesn't exist, don't crash. Just set it to null"
  - This is different from regular `@Autowired` which requires the dependency to exist
  - **Why optional?** The database might not be configured yet during startup

- **`private DataSource dataSource`** - A database connection object
  - **`DataSource`** = A pool of database connections that can be used
  - Used to test if the database is available

---

## Method: health (Lines 21-65)

```java
@GetMapping(\"/health\")
public ResponseEntity<Map<String, String>> health() {
```

### Annotation & Return Type

- **`@GetMapping(\"/health\")`** - Shorthand for handling GET requests to `/health` URL
  - When someone visits `/health`, this method runs
  - Used by monitoring systems, load balancers, Docker, Kubernetes, etc.

- **`public ResponseEntity<Map<String, String>>`** - Returns an HTTP response
  - **`ResponseEntity`** = Complete HTTP response (status + headers + body)
  - **`<Map<String, String>>`** = The body contains a Map with String keys and String values
  - Think of it as a dictionary/object with text keys and text values

---

### Line 23: Create Status Map

```java
Map<String, String> status = new HashMap<>();
```

- **`Map<String, String> status`** - Creates a variable that will hold key-value pairs
  - Example: "application" → "Spring ATS"
  - Think of it like a dictionary

- **`new HashMap<>()`** - Creates a new empty HashMap
  - **`new`** keyword = create a new object
  - **`HashMap`** = A Java class for storing key-value pairs
  - The parentheses `()` call the constructor (initializer)

---

### Line 24: Add Application Name

```java
status.put(\"application\", \"Spring ATS\");
```

- **`status.put(...)`** - Dot calls method to add a key-value pair
  - **First parameter `"application"`** = The key (like a variable name)
  - **Second parameter `"Spring ATS"`** = The value
  - After this line, the map contains: `{"application": "Spring ATS"}`

---

### Line 25: Add Current Timestamp

```java
status.put(\"timestamp\", LocalDateTime.now().toString());
```

- **`LocalDateTime.now()`** - Gets the current date and time
  - **`LocalDateTime`** = A Java class for dates and times
  - **`.now()`** = Dot calls static method to get the current time
  - Example result: `2024-12-22T14:30:45.123`

- **`.toString()`** - Another dot, converts the LocalDateTime to text
  - Makes it a String so it fits in the Map

---

### Line 26: Add Active Profile

```java
status.put(\"profile\", System.getProperty(\"spring.profiles.active\", \"default\"));
```

- **`System.getProperty(...)`** - Gets a system property (configuration setting)
  - **`"spring.profiles.active"`** = The property name to look for
  - This tells which profile the application is using (development, production, testing, etc.)

- **`\"default\"`** - If the property isn't found, use "default" instead
  - Second parameter is a fallback value

- **`status.put("profile", ...)`** - Adds the profile to the map

---

### Line 28: Log the Health Check

```java
System.out.println(\"Health check requested at: \" + LocalDateTime.now());
```

- **`System.out.println(...)`** - Prints to console (developer debugging)
- **String concatenation with `+`** - Combines text with the current time
- **Result printed:** `Health check requested at: 2024-12-22T14:30:45.123`

---

### Line 30: Check if DataSource Exists

```java
if (dataSource == null) {
```

- Checks if DataSource was successfully injected
- If DataSource is `null`, the database is not configured

---

### Lines 31-35: Handle Missing Database

```java
status.put(\"status\", \"PARTIAL\");
status.put(\"database\", \"NO_DATASOURCE\");
status.put(\"message\", \"DataSource not configured\");
System.out.println(\"Health check: DataSource is null\");
return ResponseEntity.ok(status);
```

**What this does:** If database is not available during startup:

- **`status.put("status", "PARTIAL")`** - Sets overall status to PARTIAL
  - Application is running, but database is not available

- **`status.put("database", "NO_DATASOURCE")`** - Explains database issue

- **`status.put("message", "DataSource not configured")`** - User-friendly message

- **`System.out.println(...)`** - Logs to console

- **`return ResponseEntity.ok(status)`** - Returns:
  - HTTP status 200 (OK)
  - The status map as JSON body
  - **Why 200?** The application itself is running, so don't report it as down

**JSON Response Example:**
```json
{
  "application": "Spring ATS",
  "timestamp": "2024-12-22T14:30:45",
  "profile": "default",
  "status": "PARTIAL",
  "database": "NO_DATASOURCE",
  "message": "DataSource not configured"
}
```

---

### Line 38: Start Try Block

```java
try {
```

- Starts a try-catch to handle potential database errors

---

### Line 40: Get Database Connection

```java
try (Connection connection = dataSource.getConnection()) {
```

**This is special syntax called "try-with-resources":**

- **`try (...)`** - A try block that automatically closes resources
  - Any resource opened in the parentheses gets automatically closed after the try block
  - This prevents memory leaks (forgetting to close connections)

- **`Connection connection = dataSource.getConnection()`** - Gets a database connection
  - **`dataSource.getConnection()`** - Dot calls method on dataSource
  - Obtains one connection from the database connection pool
  - **`Connection connection`** - Stores it in a variable named `connection`

**Why automatic closing?**
```
Without try-with-resources:
try {
    Connection connection = dataSource.getConnection();
    // use connection
    connection.close();  // Must remember to close!
}

With try-with-resources:
try (Connection connection = dataSource.getConnection()) {
    // use connection
}  // Connection automatically closed here!
```

---

### Line 41: Test Connection Validity

```java
if (connection.isValid(10)) {
```

- **`connection.isValid(10)`** - Dot calls method to test if connection works
  - **`10`** = Timeout in seconds (wait max 10 seconds for response)
  - Returns `true` if connection is valid
  - Returns `false` if connection is broken or times out

---

### Lines 42-43: Database is Healthy

```java
status.put(\"database\", \"UP\");
status.put(\"status\", \"UP\");
```

- If connection is valid, set both database and overall status to "UP"
- Everything is working perfectly

---

### Line 44: Log Success

```java
System.out.println(\"Health check: Database connection is healthy\");
```

- Prints success message to console

---

### Lines 45-49: Database Connection Failed

```java
} else {
    status.put(\"database\", \"DOWN\");
    status.put(\"status\", \"DEGRADED\");
    System.out.println(\"Health check: Database connection validation failed\");
}
```

- If `connection.isValid(10)` returned `false`:
  - **`status.put("database", "DOWN")`** - Database is not responding
  - **`status.put("status", "DEGRADED")`** - Application is partially working
  - Logs the issue

---

### Line 51: Return Success Response

```java
return ResponseEntity.ok(status);
```

- Returns HTTP 200 with the status map
- Browser/monitoring system gets the response

---

### Line 52: Catch SQLException

```java
} catch (SQLException e) {
```

- **`SQLException`** = SQL (database) error occurred
- **`e`** = The exception object containing error details

---

### Lines 53-56: Handle SQL Error

```java
status.put(\"status\", \"DEGRADED\");
status.put(\"database\", \"DOWN\");
status.put(\"error\", e.getMessage());
System.err.println(\"Health check: Database error - \" + e.getMessage());
```

- Sets status to DEGRADED (database is down)
- Captures the error message from the exception
- **`System.err.println(...)`** - Prints to error console (System.err is for errors)

---

### Line 57: Return OK Despite Error

```java
return ResponseEntity.ok(status);
```

- Returns HTTP 200 even though database is down
- **Why 200?** Keep the container running (Docker/Kubernetes)
  - Returning 200 tells orchestrators: "Application is still running, just no database"
  - Returning 500 would trigger restart/shutdown

---

### Line 58: Catch General Exception

```java
} catch (Exception e) {
```

- Catches any other unexpected exception (not just SQL errors)
- Catch-all for unknown problems

---

### Lines 59-62: Handle Unknown Error

```java
status.put(\"status\", \"ERROR\");
status.put(\"database\", \"UNKNOWN\");
status.put(\"error\", e.getMessage());
System.err.println(\"Health check: Unexpected error - \" + e.getMessage());
```

- Sets status to ERROR (something unexpected happened)
- Sets database to UNKNOWN (we don't know what the problem is)
- Logs the error

---

### Line 63: Return Error Response

```java
return ResponseEntity.status(503).body(status);
```

**Different from previous returns:**

- **`.status(503)`** - Sets HTTP status to 503 (Service Unavailable)
  - Only for truly unexpected errors
  - Tells monitoring system: "Something is seriously wrong"

- **`.body(status)`** - Sends the error status map

---

## Possible Responses

### Response 1: All Healthy
```
Status: 200 OK
{
  "application": "Spring ATS",
  "timestamp": "2024-12-22T14:30:45",
  "profile": "default",
  "status": "UP",
  "database": "UP"
}
```

### Response 2: Database Not Configured Yet
```
Status: 200 OK
{
  "application": "Spring ATS",
  "timestamp": "2024-12-22T14:30:45",
  "profile": "default",
  "status": "PARTIAL",
  "database": "NO_DATASOURCE",
  "message": "DataSource not configured"
}
```

### Response 3: Database Down
```
Status: 200 OK
{
  "application": "Spring ATS",
  "timestamp": "2024-12-22T14:30:45",
  "profile": "default",
  "status": "DEGRADED",
  "database": "DOWN",
  "error": "Connection refused"
}
```

### Response 4: Critical Error
```
Status: 503 Service Unavailable
{
  "application": "Spring ATS",
  "timestamp": "2024-12-22T14:30:45",
  "profile": "default",
  "status": "ERROR",
  "database": "UNKNOWN",
  "error": "OutOfMemoryException"
}
```

---

## Why This Controller Matters

**Used by:**
1. **Docker/Kubernetes** - Checks if container should stay alive
2. **Load Balancers** - Checks if server should receive traffic
3. **Monitoring Systems** - Tracks application and database health
4. **CI/CD Pipelines** - Verifies deployment succeeded
5. **Developers** - Quick check if app is running

**URL:** `http://localhost:8080/ats/health`

---

## Key Concepts

### Try-With-Resources
```java
// Old way - must remember to close
Connection conn = dataSource.getConnection();
// use connection
conn.close();

// New way - automatic closing
try (Connection conn = dataSource.getConnection()) {
    // use connection
}  // conn automatically closed
```

### HTTP Status Codes in Health Checks
```
200 → OK or DEGRADED (app is running)
503 → CRITICAL ERROR (app has serious problems)
```

### Optional Dependencies
```java
@Autowired(required = false)  // OK if not available
private DataSource dataSource;  // Will be null if not found

vs

@Autowired               // MUST exist
private DataSource dataSource;  // Crash if not found
```

### The Dot (.) Symbol
- **Method calls:** `dataSource.getConnection()`, `connection.isValid()`, `e.getMessage()`
- **Chaining:** `ResponseEntity.ok().body()`
- **Static methods:** `System.getProperty()`, `LocalDateTime.now()`

---

## Summary

**HealthController's Job:**
- Provide an endpoint that monitoring systems can check
- Report application status (UP, DEGRADED, PARTIAL, ERROR)
- Test database connection availability
- Return appropriate HTTP status codes

**Key Points:**
- Returns 200 for OK/DEGRADED (keep running)
- Returns 503 for critical errors (need restart)
- Uses try-with-resources for automatic connection closing
- DataSource injection is optional to handle startup timing
- Three possible database states: UP, DOWN, NO_DATASOURCE