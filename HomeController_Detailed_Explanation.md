# HomeController.java - Detailed Line-by-Line Explanation

## Class Declaration (Lines 38-39)

```java
@Controller
public class HomeController {
```

- **`@Controller`** - Spring annotation marking this as a MVC Controller (returns HTML pages)
- **`public class HomeController`** - A public class that handles home/user dashboard pages

**Purpose:** This controller manages user dashboard pages where users can view and update their information (assignments, profile, academic details, family info).

---

## Field Declarations (Lines 41-63)

### UserDetailRepository (Lines 41-42)
```java
@Autowired
private UserDetailRepository userDetailRepository;
```
- Repository for accessing user information from database

### AssignmentService (Lines 44-45)
```java
@Autowired
private AssignmentService assignmentService;
```
- Service for handling assignment-related operations

### SubmissionService (Lines 47-48)
```java
@Autowired
private SubmissionService submissionService;
```
- Service for handling assignment submissions

### FilePropertyConfig (Lines 50-51)
```java
@Autowired
private FilePropertyConfig filePropertyConfig;
```
- Configuration for file upload settings

### UploadFileService (Lines 53-54)
```java
@Autowired
private UploadFileService uploadFileService;
```
- Service for handling file uploads

### ProfileService (Lines 56-57)
```java
@Autowired
private ProfileService profileService;
```
- Service for user profile operations

### AcademicService (Lines 59-60)
```java
@Autowired
private AcademicService academicService;
```
- Service for academic/educational details

### RelationService (Lines 62-63)
```java
@Autowired
private RelationService relationService;
```
- Service for family relations (parents, siblings)

---

## Method 1: redirectToHome (Lines 65-69)

```java
@RequestMapping(path = \"/home\", method = RequestMethod.GET)
public ModelAndView redirectToHome(ModelAndView modelAndView) {
    modelAndView.setViewName(\"redirect:/home/assignment\");
    return modelAndView;
}
```

**What it does:** Redirects `/home` to `/home/assignment`

- **`@RequestMapping(path = \"/home\", method = RequestMethod.GET)`** - Handles GET requests to `/home`
- **`public ModelAndView redirectToHome(ModelAndView modelAndView)`** - Returns a ModelAndView
- **`modelAndView.setViewName(\"redirect:/home/assignment\")`** - Dot calls method to set redirect target
- **`return modelAndView`** - Returns the redirect response

**Result:** User visits `/home` → Gets redirected to `/home/assignment` (the main dashboard)

---

## Method 2: getHome (Lines 71-85)

```java
@RequestMapping(path = \"/home/{page}\", method = RequestMethod.GET)
public String getHome(@PathVariable(name = \"page\", required = false) String page, Model model) {
```

### Annotation & Parameters

- **`@RequestMapping(path = \"/home/{page}\", method = RequestMethod.GET)`** - Handles URLs like:
  - `/home/assignment`
  - `/home/profile`
  - `/home/academy`
  - `/home/family`

- **`@PathVariable(name = \"page\", required = false) String page`** - Gets the page type from URL

- **`public String getHome(...)`** - Returns just a String (the view name)

---

### Line 73: Get Current User

```java
UserDetail userDetail = getCurrentUser();
```

- **`getCurrentUser()`** - Dot calls method (defined at line 169)
- Gets the currently logged-in user from the database

---

### Line 74: Add Username to Model

```java
model.addAttribute(\"username\", userDetail.getUsername());
```

- **`model.addAttribute(...)`** - Dot calls method
- Makes username available to the HTML template

---

### Lines 75-83: Conditional Logic Based on Page Type

```java
if (page.contentEquals(\"assignment\")) {
    model.addAttribute(\"assignment\", assignmentService.checkPendingAssignment(userDetail.getUserUuid()));
} else if (page.contentEquals(\"profile\")) {
    model.addAttribute(\"profile\", profileService.getProfileDetails(userDetail.getUserUuid()));
} else if (page.contentEquals(\"academy\")) {
    model.addAttribute(\"academy\", academicService.getAcademicDetails(userDetail.getUserUuid()));
} else if (page.contentEquals(\"family\")) {
    model.addAttribute(\"family\", relationService.getFamilyDetails(userDetail.getUserUuid()));
}
```

**Pattern:** Check what page was requested, fetch the right data, add to model

**Line 75:**
```java
if (page.contentEquals(\"assignment\")) {
```
- Checks if page is "assignment"

**Line 76:**
```java
model.addAttribute(\"assignment\", assignmentService.checkPendingAssignment(userDetail.getUserUuid()));
```
- **`assignmentService.checkPendingAssignment(...)`** - Dot calls method
  - **`userDetail.getUserUuid()`** - Another dot, gets user's unique ID
  - Returns pending assignments for this user
- **`model.addAttribute("assignment", ...)`** - Adds to model
- HTML template can now display assignments

**Similar for other pages:**
- `profile` → Gets profile details
- `academy` → Gets academic/education details
- `family` → Gets family relations

---

### Line 84: Return View Name

```java
return \"home\";
```

- Returns "home"
- Spring looks for `home.html` template file
- The HTML displays different content based on which data is in the model

---

## Method 3: upload (Lines 87-110)

```java
@RequestMapping(path = \"/home/upload/submission\", method = RequestMethod.POST)
public ModelAndView upload(@RequestParam(\"file\") MultipartFile file,
        @RequestParam(\"assignmentId\") Integer assignmentId, ModelAndView modelView,
        RedirectAttributes redirectAttributes) throws FileException {
```

### Annotation & Parameters

- **`@RequestMapping(path = \"/home/upload/submission\", method = RequestMethod.POST)`** - Handles POST to `/home/upload/submission`

- **`@RequestParam(\"file\") MultipartFile file`** - Gets uploaded file from form

- **`@RequestParam(\"assignmentId\") Integer assignmentId`** - Gets assignment ID

- **`RedirectAttributes redirectAttributes`** - For passing messages to next page

- **`throws FileException`** - Can throw file-related exceptions

---

### Line 91: Get Current User

```java
UserDetail userDetail = getCurrentUser();
```

- Gets the logged-in user

---

### Line 92: Check If File Exists

```java
if (file != null) {
```

- Checks if user actually uploaded a file

---

### Line 93: Create Unique Filename

```java
String fileName = new Date().getTime() + \"_\" + file.getOriginalFilename();
```

- **`new Date().getTime()`** - Gets current time in milliseconds (unique timestamp)
- **`file.getOriginalFilename()`** - Dot calls method to get user's filename
- **Concatenates with `+`** - Combines them
- **Example result:** `1703082345_homework.pdf`
- **Why timestamp?** Prevents filename collisions when multiple users upload files

---

### Line 94: Build File Path

```java
Path path = Paths.get(new File(filePropertyConfig.getFilePath() + File.separator + fileName).toURI());
```

Breaking it down:
- **`filePropertyConfig.getFilePath()`** - Dot calls method to get upload directory
- **`File.separator`** - Correct path separator (/ or \)
- String concatenation with `+` - Builds full path
- **`new File(...).toURI()`** - Converts to URI format
- **`Paths.get(...)`** - Creates a Path object

---

### Line 95: Start Try Block

```java
try {
```

- Handles potential IO errors

---

### Line 96: Write File to Disk

```java
Path outputPath = Files.write(path, file.getBytes());
```

- **`Files.write(path, file.getBytes())`** - Dot calls method
  - **`file.getBytes()`** - Converts file to bytes
  - Writes bytes to disk at the path
- **`Path outputPath`** - Stores the result

---

### Lines 97-103: Save File Record & Submission

```java
if (outputPath != null) {
    UploadFile uploadFile = uploadFileService.uploadFile(fileName, file.getOriginalFilename());
    boolean result = submissionService.uploadSubmission(assignmentId, uploadFile, userDetail);
    if (result) {
        redirectAttributes.addFlashAttribute(\"message\", \"Assignment uploaded successfully\");
    }
    modelView.setViewName(\"redirect:/home/assignment\");
}
```

**Line 97:**
```java
if (outputPath != null) {
```
- Checks if file was successfully written to disk

**Line 98:**
```java
UploadFile uploadFile = uploadFileService.uploadFile(fileName, file.getOriginalFilename());
```
- **`uploadFileService.uploadFile(...)`** - Dot calls method
  - Saves file record to database (metadata about the file)
  - **Parameters:** unique filename and original filename
- Returns an UploadFile object

**Line 99:**
```java
boolean result = submissionService.uploadSubmission(assignmentId, uploadFile, userDetail);
```
- **`submissionService.uploadSubmission(...)`** - Dot calls method
  - Links the submission to the assignment
  - Links it to the user
  - Returns true if successful

**Line 100:**
```java
if (result) {
```
- Checks if submission was saved successfully

**Line 101:**
```java
redirectAttributes.addFlashAttribute(\"message\", \"Assignment uploaded successfully\");
```
- **`addFlashAttribute(...)`** - Dot calls method
  - Adds a success message that appears on the next page
  - Flash attributes live for one request only

**Line 103:**
```java
modelView.setViewName(\"redirect:/home/assignment\");
```
- Redirects back to assignment page

---

### Lines 105-106: Catch IOException

```java
} catch (IOException io) {
    throw new FileException(\"Error while submitting an assignment\");
}
```

- If file write fails, throw a FileException
- **`throw new FileException(...)`** - Throws a custom exception with message

---

### Line 109: Return Response

```java
return modelView;
```

- Returns the ModelAndView with redirect

---

## Method 4: updateProfile (Lines 112-145)

```java
@RequestMapping(path = \"/home/update/{section}\", method = RequestMethod.POST)
public ModelAndView updateProfile(@PathVariable(name = \"section\", required = false) String section,
        @ModelAttribute ProfileTemplate profile, @ModelAttribute AcademicTemplate academy,
        @ModelAttribute(\"parents\") ParentsTemplate parents, ModelAndView modelView,
        RedirectAttributes redirectAttributes) {
```

### Annotation & Parameters

- **`@RequestMapping(path = \"/home/update/{section}\", method = RequestMethod.POST)`** - Handles POST to:
  - `/home/update/profile`
  - `/home/update/academy`
  - `/home/update/family`
  - `/home/update/sibling`

- **`@PathVariable String section`** - Gets the section being updated

- **`@ModelAttribute ProfileTemplate profile`** - Binds form fields to ProfileTemplate object
  - Spring automatically fills object from form data

- **`@ModelAttribute AcademicTemplate academy`** - Same for academic info

- **`@ModelAttribute("parents") ParentsTemplate parents`** - Same, but with custom attribute name

---

### Line 117: Get Current User

```java
UserDetail userDetail = getCurrentUser();
```

---

### Lines 118-123: Update Profile

```java
if (section.contentEquals(\"profile\")) {
    boolean result = profileService.updateProfile(profile, userDetail);
    if (result) {
        redirectAttributes.addFlashAttribute(\"message\", \"Profile updated successfully\");
    }
    modelView.setViewName(\"redirect:/home/profile\");
}
```

**What it does:** If updating profile section:

- **`profileService.updateProfile(profile, userDetail)`** - Dot calls method
  - Updates the user's profile in database
  - Returns true if successful

- **`if (result)`** - Checks if update succeeded
  - **`addFlashAttribute("message", ...)`** - Adds success message

- **Redirects to `/home/profile`**

---

### Lines 124-129: Update Academic

```java
else if (section.contentEquals(\"academy\")) {
    boolean result = academicService.addAcademicDetails(academy, userDetail.getUserUuid());
    if (result) {
        redirectAttributes.addFlashAttribute(\"message\", \"Academic details successfully added\");
    }
    modelView.setViewName(\"redirect:/home/academy\");
}
```

**Same pattern:** If updating academy section:
- **`academicService.addAcademicDetails(...)`** - Dot calls method to add/update academic info
- Shows success message
- Redirects to `/home/academy`

---

### Lines 130-135: Update Family

```java
else if (section.contentEquals(\"family\")) {
    boolean result = relationService.addFamilyDetails(parents, userDetail.getUserUuid());
    if (result) {
        redirectAttributes.addFlashAttribute(\"message\", \"Family details added successfully\");
    }
    modelView.setViewName(\"redirect:/home/family\");
}
```

**Same pattern:** If updating family section:
- **`relationService.addFamilyDetails(...)`** - Dot calls method
- Updates family relations
- Redirects to `/home/family`

---

### Lines 137-143: Update Sibling

```java
else if (section.contentEquals(\"sibling\")) {
    boolean result = relationService.addSiblingDetails(parents, userDetail.getUserUuid());
    if (result) {
        redirectAttributes.addFlashAttribute(\"message\", \"Sibling added successfully\");
    }
    modelView.setViewName(\"redirect:/home/family\");
}
```

**Same pattern:** If updating sibling info:
- **`relationService.addSiblingDetails(...)`** - Dot calls method
- Adds sibling information
- Note: Still redirects to `/home/family` (siblings are part of family)

---

### Line 144: Return

```java
return modelView;
```

- Returns the ModelAndView with redirect

---

## Method 5: deleteDetails (Lines 147-167)

```java
@RequestMapping(path = \"/home/delete/{section}\", method = RequestMethod.POST)
public ModelAndView deleteDetails(@PathVariable(name = \"section\", required = false) String section,
        @RequestParam(name = \"academy_id\", required = false) Integer academicId,
        @RequestParam(name = \"sibling_id\", required = false) Integer siblingId, ModelAndView modelView,
        RedirectAttributes redirectAttributes) {
```

### Annotation & Parameters

- **`@RequestMapping(path = \"/home/delete/{section}\", method = RequestMethod.POST)`** - Handles POST to `/home/delete/{section}`

- **`@PathVariable String section`** - Gets which section to delete from (academy or sibling)

- **`@RequestParam Integer academicId`** - Optional, ID of academic record to delete

- **`@RequestParam Integer siblingId`** - Optional, ID of sibling to delete

---

### Line 152: Get Current User

```java
UserDetail userDetail = getCurrentUser();
```

---

### Lines 153-158: Delete Academic Details

```java
if (section.contentEquals(\"academy\")) {
    boolean result = academicService.deleteAcademicDetails(academicId, userDetail.getUserUuid());
    if (result) {
        redirectAttributes.addFlashAttribute(\"message\", \"Academic details updated successfully\");
    }
    modelView.setViewName(\"redirect:/home/academy\");
}
```

**What it does:** If deleting academy record:

- **`academicService.deleteAcademicDetails(academicId, ...)`** - Dot calls method
  - Deletes the academic record by ID
  - Verifies it belongs to current user
  - Returns true if successful

- Shows success message
- Redirects to `/home/academy`

---

### Lines 159-165: Delete Sibling

```java
else if (section.contentEquals(\"sibling\")) {
    boolean result = relationService.deleteSiblings(siblingId, userDetail.getUserUuid());
    if (result) {
        redirectAttributes.addFlashAttribute(\"message\", \"Sibling's updated successfully\");
    }
    modelView.setViewName(\"redirect:/home/family\");
}
```

**Same pattern:** If deleting sibling:

- **`relationService.deleteSiblings(siblingId, ...)`** - Dot calls method to delete
- Shows success message
- Redirects to `/home/family`

---

### Line 166: Return

```java
return modelView;
```

---

## Method 6: getCurrentUser (Lines 169-173)

```java
public UserDetail getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserDetail userDetail = userDetailRepository.findByEmailEquals(username);
    return userDetail;
}
```

**Helper method used by other methods:**

- **`public`** - Can be called from anywhere
- **`UserDetail getCurrentUser()`** - Returns the current logged-in user

**Line 170:**
```java
String username = SecurityContextHolder.getContext().getAuthentication().getName();
```
- Gets logged-in user's email
- Same as in AdminController

**Line 171:**
```java
UserDetail userDetail = userDetailRepository.findByEmailEquals(username);
```
- Finds the full user record in database

**Line 172:**
```java
return userDetail;
```
- Returns the user object

---

## Class Closing (Line 175)

```java
}
```

---

## Common Patterns in This Controller

### Pattern 1: Get Current User & Check Authorization
```java
UserDetail userDetail = getCurrentUser();
```

### Pattern 2: Conditional Logic for Different Sections
```java
if (page.contentEquals("assignment")) {
    // handle assignment
} else if (page.contentEquals("profile")) {
    // handle profile
}
```

### Pattern 3: CRUD Operations
```java
// Create/Update
boolean result = service.add/update(data, user);
if (result) {
    addFlashAttribute("message", "Success");
}

// Delete
boolean result = service.delete(id, user);
if (result) {
    addFlashAttribute("message", "Deleted");
}
```

### Pattern 4: File Upload & Save
```java
Path outputPath = Files.write(path, file.getBytes());
if (outputPath != null) {
    UploadFile uploadFile = uploadFileService.uploadFile(...);
    boolean result = submissionService.uploadSubmission(...);
}
```

---

## Page Flow Diagram

```
/home
    ↓
redirect to /home/assignment
    ↓
/home/assignment → getHome() → Shows assignments
/home/profile → getHome() → Shows profile
/home/academy → getHome() → Shows academic details
/home/family → getHome() → Shows family info

/home/update/profile (POST) → updateProfile() → Updates profile → Redirect to /home/profile
/home/update/academy (POST) → updateProfile() → Updates academy → Redirect to /home/academy
/home/update/family (POST) → updateProfile() → Updates family → Redirect to /home/family
/home/update/sibling (POST) → updateProfile() → Updates sibling → Redirect to /home/family

/home/delete/academy (POST) → deleteDetails() → Deletes academy → Redirect to /home/academy
/home/delete/sibling (POST) → deleteDetails() → Deletes sibling → Redirect to /home/family

/home/upload/submission (POST) → upload() → Saves file & submission → Redirect to /home/assignment
```

---

## Summary

**HomeController's Job:**
- Display user dashboard with multiple sections
- Handle profile, academic, family, and assignment information
- Upload assignment submissions
- Update user information
- Delete academic and family records

**Key Points:**
- Four main sections: assignment, profile, academy, family
- Uses getCurrentUser() helper to get logged-in user
- Follows CRUD pattern (Create, Read, Update, Delete)
- Flash attributes for success messages
- File uploads handled with unique timestamps
- Service layer does actual database operations