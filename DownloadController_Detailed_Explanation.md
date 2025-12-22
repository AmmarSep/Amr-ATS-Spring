# DownloadController.java - Detailed Line-by-Line Explanation

## Class Declaration (Lines 28-29)

```java
@RestController
public class DownloadController {
```

- **`@RestController`** - This is a Spring Framework annotation that marks this class as a REST Controller
  - **REST** = "Representational State Transfer" (a way to build web APIs)
  - Unlike `@Controller` (which returns HTML pages), `@RestController` returns data (JSON, files, bytes, etc.)
  - This controller is specifically designed to handle file downloads and viewing

- **`public class DownloadController`** - Declares a public class named `DownloadController`

---

## Understanding REST vs Regular Controller

| Regular @Controller | @RestController |
|---|---|
| Returns HTML pages | Returns data/files/JSON |
| Used for traditional web apps | Used for APIs and file downloads |
| Returns view name (String) | Returns ResponseEntity or other data |
| Renders Thymeleaf templates | Sends raw data back |

---

## Field Declarations (Lines 31-38)

### ResourceLoader (Lines 31-32)
```java
@Autowired
ResourceLoader resourceLoader;
```

- **`@Autowired`** - Spring automatically provides this object
- **`ResourceLoader resourceLoader`** - A Spring class that loads resources (files, templates, etc.)
- **Purpose:** Used to load files from the classpath (the application's internal files)
  - Example: Loading a template file stored inside the application package

### UploadFileRepository (Lines 34-35)
```java
@Autowired
private UploadFileRepository uploadFileRepository;
```

- **`private UploadFileRepository uploadFileRepository`** - Repository for accessing uploaded file information from the database
- **Purpose:** Get file details (like original filename, file path) from the database

### FilePropertyConfig (Lines 37-38)
```java
@Autowired
private FilePropertyConfig filePropertyConfig;
```

- **`private FilePropertyConfig filePropertyConfig`** - Configuration object for file paths
- **Purpose:** Tells the application where uploaded files are stored on the disk

---

## Method 1: downloadFile (Lines 40-75)

```java
@RequestMapping(path = \"/download/{file}\", method = RequestMethod.GET)
public ResponseEntity<byte[]> downloadFile(@PathVariable String file,
        @RequestParam(name = \"id\", required = false) Integer id, HttpServletRequest request)
        throws FileNotFoundException, IOException {
```

### Annotation & Parameters Breakdown

- **`@RequestMapping(path = \"/download/{file}\", method = RequestMethod.GET)`** - This handles GET requests to URLs like:
  - `/download/users`
  - `/download/attachment`
  - **`{file}`** is a placeholder for the file type

- **`public ResponseEntity<byte[]>`** - This method returns a ResponseEntity containing bytes
  - **`ResponseEntity`** = An HTTP response object (status code, headers, body)
  - **`<byte[]>`** = The body contains bytes (raw file data)
  - Think of it as a package being sent to the browser with:
    - Status code (200 = OK)
    - Headers (file type, download settings)
    - Body (the actual file data)

- **`@PathVariable String file`** - Gets the file type from the URL
  - If URL is `/download/users`, then `file = "users"`
  - If URL is `/download/attachment`, then `file = "attachment"`

- **`@RequestParam(name = \"id\", required = false) Integer id`** - Gets an optional ID from the query string
  - If URL is `/download/attachment?id=5`, then `id = 5`
  - **`required = false`** means this parameter is optional

- **`HttpServletRequest request`** - The HTTP request object (contains browser info, headers, etc.)

- **`throws FileNotFoundException, IOException`** - This method can throw two types of exceptions:
  - **`FileNotFoundException`** - File doesn't exist
  - **`IOException`** - Error reading/writing the file

---

### Lines 44-46: Variable Declarations

```java
Resource resource = null;
String originalFileName = null;
InputStreamResource streamResource = null;
```

Creating three variables to hold:
- **`Resource resource`** - Will hold a file resource from the classpath
- **`String originalFileName`** - Will hold the original name of the file (for display)
- **`InputStreamResource streamResource`** - Will hold the file as a stream (sequence of bytes we can send)

All initialized to `null` (empty) because we don't know which file yet.

---

### Line 48: Start Try Block

```java
try {
```

- Starts a try-catch block to handle potential errors
- Any IO (input/output) errors inside will be caught at line 63

---

### Lines 49-53: Handle "users" File Type

```java
if (file.contentEquals(\"users\")) {
    resource = resourceLoader.getResource(\"classpath:format/user_template.json\");
    originalFileName = resource.getFilename();
    streamResource = new InputStreamResource(new FileInputStream(resource.getFile()));
}
```

**What this does:** If the user is downloading a user template file:

**Line 49:**
```java
if (file.contentEquals(\"users\")) {
```
- **`file.contentEquals(\"users\")`** - Dot calls method to check if `file` equals "users"
- If URL was `/download/users`, this is true

**Line 50:**
```java
resource = resourceLoader.getResource(\"classpath:format/user_template.json\");
```
- **`resourceLoader.getResource(...)`** - Dot calls method on resourceLoader
  - **`\"classpath:format/user_template.json\"`** = A path inside the application
    - **`classpath:`** means "inside the application package"
    - **`format/user_template.json`** = Folder path and filename
- **What it does:** Loads the user template JSON file from inside the application
- **Stores it in:** `resource` variable

**Line 51:**
```java
originalFileName = resource.getFilename();
```
- **`resource.getFilename()`** - Dot calls method to get the filename
  - Returns "user_template.json"
- **Stores it in:** `originalFileName` variable

**Line 52:**
```java
streamResource = new InputStreamResource(new FileInputStream(resource.getFile()));
```

Breaking it down from inside out:

- **`resource.getFile()`** - Dot calls method to get the file object
- **`new FileInputStream(...)`** - Creates a stream to read bytes from the file
  - **`new`** keyword creates a new object
  - **`FileInputStream`** = A Java class for reading file bytes
- **`new InputStreamResource(...)`** - Wraps the stream in an InputStreamResource
  - **`InputStreamResource`** = A Spring class that makes the stream easy to work with for HTTP responses
- **Stores it in:** `streamResource` variable

**End result:** We now have:
- `originalFileName` = "user_template.json"
- `streamResource` = The file ready to be sent as bytes

---

### Lines 54-62: Handle "attachment" File Type

```java
if (file.contentEquals(\"attachment\")) {
    Optional<UploadFile> referenceFile = uploadFileRepository.findById(id);
    if (referenceFile.isPresent()) {
        File downloadFile = new File(
                filePropertyConfig.getFilePath() + File.separator + referenceFile.get().getFileName());
        originalFileName = referenceFile.get().getFileOriginalName();
        streamResource = new InputStreamResource(new FileInputStream(downloadFile));
    }
}
```

**What this does:** If the user is downloading an uploaded file (like a resume):

**Line 54:**
```java
if (file.contentEquals(\"attachment\")) {
```
- Checks if file type is "attachment"

**Line 55:**
```java
Optional<UploadFile> referenceFile = uploadFileRepository.findById(id);
```

- **`uploadFileRepository.findById(id)`** - Dot calls method to find a file record by ID
  - **`id`** is the query parameter we got from the URL (like `?id=5`)
  - This searches the database for the file record
- **`Optional<UploadFile>`** - The result might be found or might not exist
  - **`Optional`** = A container that either has a value or is empty
  - If file with ID 5 exists, it's inside the Optional
  - If file doesn't exist, the Optional is empty
- **Stores it in:** `referenceFile` variable

**Line 56:**
```java
if (referenceFile.isPresent()) {
```

- **`referenceFile.isPresent()`** - Dot calls method to check if the Optional contains a file
  - Returns `true` if a file was found
  - Returns `false` if no file was found
- **If true:** Execute the code inside the braces

**Lines 57-58:**
```java
File downloadFile = new File(
        filePropertyConfig.getFilePath() + File.separator + referenceFile.get().getFileName());
```

Breaking it down:

- **`referenceFile.get().getFileName()`**
  - **`.get()`** - Dot calls method to extract the UploadFile from the Optional
    - **Only works if file exists (we checked `isPresent()` first)**
  - **`.getFileName()`** - Dot calls method to get the stored filename
    - Returns something like "1703082345_resume.pdf"

- **`filePropertyConfig.getFilePath()`** - Dot calls method to get the upload directory
  - Returns something like "/uploads" or "C:\uploads"

- **`File.separator`** - The correct path separator for the operating system
  - On Mac/Linux: `/`
  - On Windows: `\`

- **String concatenation with `+`** - Combines all parts into a full path
  - Example result: `/uploads/1703082345_resume.pdf`

- **`new File(...)`** - Creates a File object pointing to that location
- **Stores it in:** `downloadFile` variable

**Line 59:**
```java
originalFileName = referenceFile.get().getFileOriginalName();
```

- **`referenceFile.get().getFileOriginalName()`** - Dot calls method to get the original filename
  - Returns the name the user gave it (like "MyResume.pdf")
  - This is what appears in the download dialog
- **Stores it in:** `originalFileName` variable

**Line 60:**
```java
streamResource = new InputStreamResource(new FileInputStream(downloadFile));
```

Same as before:
- Creates a FileInputStream from the file
- Wraps it in InputStreamResource
- Stores in `streamResource`

---

### Lines 63-65: Catch IOException

```java
} catch (IOException ex) {
    System.out.println(\"Could not determine file type.\");
}
```

- **`catch (IOException ex)`** - If any IOException occurs in the try block:
- **`System.out.println(...)`** - Prints a message to the console (developer debugging)
- The error is caught and handled gracefully (no crash)

---

### Lines 67-69: Check if File Was Found

```java
if (streamResource == null) {
    String errorResponse = new String(\"{\\\"message\\\" : \\\"Request error\\\"}\");
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(errorResponse.getBytes());
}
```

**What this does:** If we couldn't load the file (streamResource is still null):

**Line 67:**
```java
if (streamResource == null) {
```
- Checks if we successfully loaded a file
- If `streamResource` is still `null`, it means no file was found

**Line 68:**
```java
String errorResponse = new String(\"{\\\"message\\\" : \\\"Request error\\\"}\");
```

- Creates a JSON string with an error message
  - **`\"\\\"message\\\"\"`** - The backslashes escape the quotes so JSON is valid
  - **Actual JSON:** `{"message" : "Request error"}`

**Line 69:**
```java
return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(errorResponse.getBytes());
```

Breaking it down:

- **`ResponseEntity.ok()`** - Dot calls method to create a response with status 200 (OK)
  - The user gets HTTP 200, but the error message explains what went wrong

- **`.contentType(MediaType.APPLICATION_JSON)`** - Another dot, sets the content type
  - Tells the browser: "This is JSON data"
  - MediaType.APPLICATION_JSON = "application/json"

- **`.body(errorResponse.getBytes())`** - Another dot, sets the response body
  - **`.getBytes()`** - Converts the JSON string to bytes
  - Sends the error JSON back to the browser

---

### Lines 70-74: Return File Successfully

```java
} else {
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, \"attachment; filename=\\\"\" + originalFileName + \"\\\"\"  )
            .body(streamResource.getInputStream().readAllBytes());
}
```

**What this does:** If the file was found, send it to the browser:

**Line 71:**
```java
return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
```

- **`ResponseEntity.ok()`** - Creates response with status 200 (OK)
- **`.contentType(MediaType.APPLICATION_OCTET_STREAM)`** - Another dot
  - Sets content type to "application/octet-stream"
  - This tells the browser: "This is a binary file (any type of file)"
  - Browsers treat this as a download

**Line 72:**
```java
.header(HttpHeaders.CONTENT_DISPOSITION, \"attachment; filename=\\\"\" + originalFileName + \"\\\"\"  )
```

- **`.header(...)`** - Another dot, adds an HTTP header
  - **`HttpHeaders.CONTENT_DISPOSITION`** - A header name that tells the browser what to do
  - **`"attachment; filename=..."` value** - Tells browser: "This is an attachment, download it with this name"
  - **String concatenation:** `"attachment; filename=\"" + originalFileName + "\""`
    - If originalFileName is "resume.pdf"
    - Result: `attachment; filename="resume.pdf"`
  - The backslashes escape the quote marks in the string

**Visual Example:**
```
Header: Content-Disposition: attachment; filename="resume.pdf"
Browser sees this and:
  1. Knows it's a file to download
  2. Shows a download dialog
  3. Suggests "resume.pdf" as the filename
```

**Line 73:**
```java
.body(streamResource.getInputStream().readAllBytes());
```

- **`.body(...)`** - Another dot, sets the response body (the actual file data)
- **`streamResource.getInputStream()`** - Dot calls method to get the input stream
- **`.readAllBytes()`** - Another dot, converts all the file bytes into a byte array
  - Reads the entire file into memory as bytes
- Sends these bytes to the browser

---

## Method 2: viewFile (Lines 77-113)

```java
@RequestMapping(path = \"/view/attachment\", method = RequestMethod.GET)
public ResponseEntity<byte[]> viewFile(@RequestParam(\"id\") Integer id, HttpServletRequest request)
        throws FileNotFoundException, IOException {
```

### Annotation & Parameters

- **`@RequestMapping(path = \"/view/attachment\", method = RequestMethod.GET)`** - Handles GET requests to `/view/attachment?id=5`
  - **`/view/`** = View the file in the browser (not download)
  - Different from `/download/` which forces downloading

- **`@RequestParam(\"id\") Integer id`** - Gets the ID from query string (required, not optional)
  - URL must be `/view/attachment?id=5`

- **`throws FileNotFoundException, IOException`** - Can throw two types of exceptions

---

### Lines 80-81: Variable Declarations

```java
InputStreamResource streamResource = null;
String contentType = \"application/pdf\"; // Default to PDF
```

- **`InputStreamResource streamResource = null;`** - Will hold the file stream
- **`String contentType = \"application/pdf\";`** - Sets default content type to PDF
  - **`// Default to PDF`** - This is a comment explaining the default

---

### Line 83: Start Try Block

```java
try {
```

- Starts try-catch to handle IO errors

---

### Line 84: Find File in Database

```java
Optional<UploadFile> referenceFile = uploadFileRepository.findById(id);
```

- Same as before:
  - Finds the file record by ID in the database
  - Result is wrapped in Optional (might exist or not)

---

### Lines 85-98: If File Found, Load It and Detect Type

```java
if (referenceFile.isPresent()) {
    File viewFile = new File(
            filePropertyConfig.getFilePath() + File.separator + referenceFile.get().getFileName());
    streamResource = new InputStreamResource(new FileInputStream(viewFile));

    // Determine content type based on file extension
    String fileName = referenceFile.get().getFileOriginalName().toLowerCase();
    if (fileName.endsWith(\".pdf\")) {
        contentType = \"application/pdf\";
    } else if (fileName.endsWith(\".doc\") || fileName.endsWith(\".docx\")) {
        contentType = \"application/vnd.openxmlformats-officedocument.wordprocessingml.document\";
    } else if (fileName.endsWith(\".txt\")) {
        contentType = \"text/plain\";
    }
}
```

**Line 85:**
```java
if (referenceFile.isPresent()) {
```
- Checks if file record was found

**Lines 86-87:**
```java
File viewFile = new File(
        filePropertyConfig.getFilePath() + File.separator + referenceFile.get().getFileName());
```
- Same as downloadFile method: Builds the full file path and creates a File object

**Line 88:**
```java
streamResource = new InputStreamResource(new FileInputStream(viewFile));
```
- Creates a stream from the file (same as before)

**Line 91:**
```java
String fileName = referenceFile.get().getFileOriginalName().toLowerCase();
```

- **`.getFileOriginalName()`** - Dot calls method to get filename (like "MyResume.pdf")
- **`.toLowerCase()`** - Another dot, converts to lowercase (like "myresume.pdf")
  - Makes comparison case-insensitive (doesn't matter if ".PDF" or ".pdf")
- Stores result in `fileName` variable

**Lines 92-98: Content Type Detection**

```java
if (fileName.endsWith(\".pdf\")) {
    contentType = \"application/pdf\";
} else if (fileName.endsWith(\".doc\") || fileName.endsWith(\".docx\")) {
    contentType = \"application/vnd.openxmlformats-officedocument.wordprocessingml.document\";
} else if (fileName.endsWith(\".txt\")) {
    contentType = \"text/plain\";
}
```

**Why this is different from downloadFile:**
- downloadFile forces a download for everything
- viewFile displays the file in the browser
- Different file types need different content types for proper display

**Line 92:**
```java
if (fileName.endsWith(\".pdf\")) {
```
- **`fileName.endsWith(\".pdf\")`** - Dot calls method to check if filename ends with ".pdf"
- **`.endsWith(...)`** - Checks the ending characters

**If PDF:**
```java
contentType = \"application/pdf\";
```
- Sets content type for PDF
- Browser will display PDF inline (inside the page)

**Line 94:**
```java
} else if (fileName.endsWith(\".doc\") || fileName.endsWith(\".docx\")) {
```

- **`||`** = "OR" operator (either condition can be true)
- Checks if filename ends with ".doc" OR ".docx"
- If true, sets content type for Word documents

**Line 95:**
```java
contentType = \"application/vnd.openxmlformats-officedocument.wordprocessingml.document\";
```

- This is the official MIME type for .docx files
- It's a long name because Microsoft's Office Open XML standard has a formal name
- Tells browser: "This is a Word document"

**Line 96:**
```java
} else if (fileName.endsWith(\".txt\")) {
```

- Checks if filename ends with ".txt"

**Line 97:**
```java
contentType = \"text/plain\";
```

- Sets content type for plain text files
- Browser displays as text

---

### Lines 100-102: Catch IOException

```java
} catch (IOException ex) {
    System.out.println(\"Could not determine file type.\");
}
```

- Same as in downloadFile method
- Catches any IO errors and prints a message

---

### Lines 104-106: Check if File Was Found

```java
if (streamResource == null) {
    String errorResponse = new String(\"{\\\"message\\\" : \\\"File not found\\\"}\");
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(errorResponse.getBytes());
}
```

- Same logic as downloadFile
- If file wasn't found, return JSON error message
- Message says "File not found" instead of "Request error"

---

### Lines 107-112: Return File for Viewing

```java
} else {
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, \"inline\")
            .body(streamResource.getInputStream().readAllBytes());
}
```

**Similar to downloadFile, but with important differences:**

**Line 109:**
```java
.contentType(MediaType.parseMediaType(contentType))
```

- **`.parseMediaType(contentType)`** - Dot calls method to parse the content type
  - Takes a string like "application/pdf" and converts it to a MediaType object
  - This is flexible and uses the `contentType` variable we set above
  - Different file types use different content types (set by the detection code)

**Line 110:**
```java
.header(HttpHeaders.CONTENT_DISPOSITION, \"inline\")
```

**KEY DIFFERENCE from downloadFile:**
- downloadFile uses: `"attachment; filename=..."`
  - Forces the browser to download the file
- viewFile uses: `"inline"`
  - Tells the browser to display the file inline (inside the page)
  - PDFs show as embedded PDFs
  - Text files show as text
  - Word docs might open in the Word viewer or as attachment depending on browser

**Line 111:**
```java
.body(streamResource.getInputStream().readAllBytes());
```

- Same as downloadFile: Sends the file bytes to the browser

---

## Class Closing (Line 115)

```java
}
```

- Closing brace that ends the `DownloadController` class

---

## Key Differences: downloadFile vs viewFile

| Feature | downloadFile | viewFile |
|---------|---|---|
| **URL** | `/download/{file}` | `/view/attachment` |
| **File Type** | users, attachment | attachment only |
| **ID Parameter** | Optional | Required |
| **Content Disposition** | `attachment` (forces download) | `inline` (displays in browser) |
| **Content Type Detection** | Basic (all files) | Advanced (PDF, Word, Text) |
| **Browser Behavior** | Shows download dialog | Opens file in browser or plugin |

---

## How HTTP Headers Control File Behavior

### For Download (downloadFile)
```
Content-Disposition: attachment; filename="resume.pdf"
Content-Type: application/octet-stream

Browser sees this and:
  → Shows download dialog
  → Suggests filename "resume.pdf"
  → Saves to Downloads folder
```

### For View (viewFile)
```
Content-Disposition: inline
Content-Type: application/pdf

Browser sees this and:
  → Displays PDF plugin/viewer
  → Shows file inline on page
  → User can scroll and navigate
```

---

## Request Flow Examples

### Example 1: Download User Template
```
User visits: /download/users

Flow:
1. @RequestMapping catches the request
2. file = "users"
3. Check: file.contentEquals("users") → TRUE
4. Load: classpath:format/user_template.json
5. Get filename: "user_template.json"
6. Create FileInputStream
7. Create InputStreamResource
8. Set Content-Disposition: attachment; filename="user_template.json"
9. Send file bytes to browser
10. Browser downloads file as "user_template.json"
```

### Example 2: Download Resume (attachment)
```
User visits: /download/attachment?id=5

Flow:
1. @RequestMapping catches the request
2. file = "attachment"
3. id = 5
4. Check: file.contentEquals("attachment") → TRUE
5. Query database: Find UploadFile with id=5
6. Found: UploadFile with fileName="1703082345_resume.pdf", originalName="MyResume.pdf"
7. Build path: /uploads/1703082345_resume.pdf
8. Create FileInputStream from that path
9. Create InputStreamResource
10. Set Content-Disposition: attachment; filename="MyResume.pdf"
11. Send file bytes to browser
12. Browser downloads file as "MyResume.pdf"
```

### Example 3: View PDF in Browser
```
User visits: /view/attachment?id=5

Flow:
1. @RequestMapping catches the request
2. id = 5 (required)
3. Query database: Find UploadFile with id=5
4. Found: UploadFile with fileName="1703082345_resume.pdf", originalName="Resume.pdf"
5. Build path: /uploads/1703082345_resume.pdf
6. Create FileInputStream
7. Create InputStreamResource
8. fileName.toLowerCase() = "resume.pdf"
9. fileName.endsWith(".pdf") → TRUE
10. contentType = "application/pdf"
11. Set Content-Disposition: inline
12. Set Content-Type: application/pdf
13. Send file bytes to browser
14. Browser displays PDF plugin showing the file inline
```

---

## Why Two Methods?

**Different Use Cases:**

1. **Download Method:**
   - User wants to save the file
   - Works with both template files (classpath) and uploaded files (database)
   - Forces browser to download
   - Preserves original filename

2. **View Method:**
   - User wants to preview the file
   - Works with uploaded files only
   - Displays in browser using appropriate viewer
   - Detects file type automatically
   - Supports PDF, Word, and Text files

---

## Key Concepts Used

### ResponseEntity
```java
ResponseEntity = HTTP Response = Status + Headers + Body

Example:
ResponseEntity.ok()                          // Status 200
    .contentType(MediaType.APPLICATION_PDF) // Header: Content-Type
    .header(HttpHeaders.CONTENT_DISPOSITION, "inline") // Header: Content-Disposition
    .body(fileBytes)                         // Body: Actual file bytes
```

### Optional
```java
Optional = A container that might have a value or be empty

Optional<UploadFile> referenceFile = repository.findById(5);

if (referenceFile.isPresent()) {    // Check if value exists
    UploadFile file = referenceFile.get();  // Extract the value
}
```

### The Dot (.) Symbol in This File
- Method calls: `file.contentEquals()`, `resourceLoader.getResource()`, `streamResource.getInputStream()`
- Chained methods: `ResponseEntity.ok().contentType().header().body()`
- Field access: `RequestDispatcher.ERROR_STATUS_CODE`

### String Concatenation
```java
String path = filePropertyConfig.getFilePath() + File.separator + fileName;

Example:
"/uploads" + "/" + "resume.pdf" = "/uploads/resume.pdf"
```

### Try-Catch
```java
try {
    // Code that might throw IOException
    File file = new File(path);
    new FileInputStream(file);
} catch (IOException ex) {
    // Handle the error gracefully
    System.out.println("Error: " + ex.getMessage());
}
```

---

## Summary

**DownloadController's Job:**
- Handle file downloads and viewing
- Load files from classpath (templates) or database (uploaded files)
- Send files to browser with correct headers and content types
- Support both download (save) and view (display) modes

**Two Methods:**
1. **downloadFile()** - Downloads files, forces save dialog
2. **viewFile()** - Views files, displays in browser with auto file-type detection

**Key Headers:**
- `Content-Disposition: attachment` → Browser downloads
- `Content-Disposition: inline` → Browser displays
- `Content-Type` → Browser knows what type of file it is