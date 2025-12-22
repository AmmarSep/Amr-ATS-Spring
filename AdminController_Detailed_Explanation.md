# AdminController.java - Detailed Line-by-Line Explanation

## Class Declaration (Line 48-49)

```java
@Controller
public class AdminController {
```

- **`@Controller`** - This is a Spring Framework annotation that marks this class as a "Controller". In the MVC pattern, a Controller handles incoming requests from users and decides what to do with them.
- **`public class AdminController`** - This declares a public class named `AdminController`. Any class can use/access this class because it's marked as `public`.

---

## Field Declarations (Lines 51-82)

These are class variables that store references to important objects the controller needs to work with. The `@Autowired` annotation tells Spring to automatically inject (provide) these dependencies.

### UserDetail Repository (Lines 51-52)
```java
@Autowired
private UserDetailRepository userDetailRepository;
```
- **`@Autowired`** - Tells Spring to automatically find and assign an object that implements `UserDetailRepository` interface
- **`private`** - Only this class can access this variable
- **`UserDetailRepository userDetailRepository`** - A variable that can perform database operations related to users (like finding users, saving users, etc.)

### StaffDetail Repository (Lines 54-55)
```java
@Autowired
private StaffDetailRepository staffDetailRepository;
```
- Same as above, but for staff members (teachers/instructors)

### CourseList Repository (Lines 57-58)
```java
@Autowired
private CourseListRepository courseListRepository;
```
- Repository for managing courses

### AssignmentDetail Repository (Lines 60-61)
```java
@Autowired
private AssignmentDetailRepository assignmentDetailRepository;
```
- Repository for managing assignments

### JobPosting Repository (Lines 63-64)
```java
@Autowired
private JobPostingRepository jobPostingRepository;
```
- Repository for managing job postings

### Application Repository (Lines 66-67)
```java
@Autowired
private ApplicationRepository applicationRepository;
```
- Repository for managing job applications from candidates

### FilePropertyConfig (Lines 69-70)
```java
@Autowired
private FilePropertyConfig filePropertyConfig;
```
- Configuration object that holds settings for file uploads (like where to save files)

### UserService (Lines 72-73)
```java
@Autowired
private UserService userService;
```
- Service object that contains business logic for user-related operations
- Services contain the actual logic, while repositories only handle database access

### StaffService (Lines 75-76)
```java
@Autowired
private StaffService staffService;
```
- Service object for staff-related business logic

### CourseService (Lines 78-79)
```java
@Autowired
private CourseService courseService;
```
- Service object for course-related business logic

### AssignmentService (Lines 81-82)
```java
@Autowired
private AssignmentService assignmentService;
```
- Service object for assignment-related business logic

---

## Method 1: redirectAdminHome (Lines 84-88)

```java
@RequestMapping(path = "/admin", method = RequestMethod.GET)
public ModelAndView redirectAdminHome(ModelAndView modelAndView) {
    modelAndView.setViewName("redirect:/admin/users");
    return modelAndView;
}
```

**Explanation:**
- **`@RequestMapping(path = "/admin", method = RequestMethod.GET)`** - This annotation says: "When a user requests `/admin` URL using GET method (like typing in browser), run this method"
- **`public`** - Any code can call this method
- **`ModelAndView`** - An object that holds data to display and the name of the HTML page to show
  - **`ModelAndView modelAndView`** - This parameter is passed in by Spring
- **`modelAndView.setViewName("redirect:/admin/users")`** - The dot (`.`) means we're calling the `setViewName` method on the `modelAndView` object
  - `"redirect:/admin/users"` means: don't show a page directly, instead redirect (send) the user to the `/admin/users` URL
- **`return modelAndView`** - Send this object back to Spring so it knows what to do

**Purpose:** When someone visits `/admin`, they get automatically redirected to `/admin/users` page.

---

## Method 2: getAdminHome (Lines 90-115)

```java
@RequestMapping(path = "/admin/{page}", method = RequestMethod.GET)
public String getAdminHome(@PathVariable(\"page\") String page, Model model) {
```

**Annotations & Parameters:**
- **`@RequestMapping(path = "/admin/{page}", method = RequestMethod.GET)`** - Handles URLs like `/admin/users`, `/admin/jobs`, etc.
  - **`{page}`** - This is a placeholder. Whatever word is in that position becomes the `page` variable
- **`public String`** - This method returns a String (the name of the HTML file to show)
- **`@PathVariable(\"page\") String page`** - The dot (`.`) in `@PathVariable` means this annotation comes from Spring framework
  - This tells Spring: "Take the `{page}` part from the URL and give it to this `page` variable"
- **`Model model`** - An object to hold data that will be displayed on the HTML page

### Lines 91-94: Getting Current User
```java
String username = SecurityContextHolder.getContext().getAuthentication().getName();
UserDetail userDetail = userDetailRepository.findByEmailEquals(username);
model.addAttribute("username", userDetail.getUsername());
```

Breaking down line 92:
- **`SecurityContextHolder`** - A class that stores information about who is currently logged in
- **`.getContext()`** - The dot means we're calling the `getContext()` method. This gets the security information
- **`.getAuthentication()`** - Another dot. This gets the authentication object (who logged in)
- **`.getName()`** - Another dot. Gets the logged-in user's name/email

So the entire line gets the email of the currently logged-in user and stores it in the `username` variable.

Line 93:
- **`userDetailRepository.findByEmailEquals(username)`** - The dot means we're calling a method on the repository
  - This searches the database for a user with that email
  - The result is stored in `userDetail` variable
- **`model.addAttribute("username", userDetail.getUsername())`** - The first dot calls `addAttribute` method
  - The dot in `userDetail.getUsername()` gets the username from the user object
  - This adds the username to the `model` so it can be displayed on the HTML page

### Lines 95-113: Conditional Logic Based on Page
```java
if (page.contentEquals("users")) {
    List<UserDetail> userDetails = userDetailRepository.findByEmailNot("admin@spring.ats");
    model.addAttribute("users", userDetails);
} else if (page.contentEquals("jobs")) {
    List<JobPosting> jobPostings = jobPostingRepository.findAll();
    model.addAttribute("jobs", jobPostings);
} else if (page.contentEquals("applications")) {
    List<Application> applications = applicationRepository.findAllWithResumeAndDetails();
    model.addAttribute("applications", applications);
} else if (page.contentEquals("hired-candidates")) {
    List<Application> hiredCandidates = applicationRepository.findByStatusWithResumeAndDetails("Hired");
    model.addAttribute("hiredCandidates", hiredCandidates);
} else if (page.contentEquals("interview-scheduled")) {
    List<Application> interviewScheduledCandidates = applicationRepository.findByStatusWithResumeAndDetails("Interview");
    model.addAttribute("interviewScheduledCandidates", interviewScheduledCandidates);
} else if (page.contentEquals("recruiters")) {
    List<UserDetail> recruiters = userDetailRepository.findByUserGroupShortGroupEquals("REC");
    model.addAttribute("recruiters", recruiters);
}
```

**General Pattern:** Check what page was requested, fetch the right data, and add it to the model.

- **`page.contentEquals("users")`** - Dot means we're calling the `contentEquals` method
  - This checks if the `page` variable equals the string `"users"`
- **`List<UserDetail>`** - This declares a list (like an array) of `UserDetail` objects
- **`userDetailRepository.findByEmailNot("admin@spring.ats")`** - Gets all users EXCEPT the admin user
  - The dot calls the `findByEmailNot` method on the repository
- **`model.addAttribute("users", userDetails)`** - Dot calls `addAttribute` method
  - Adds the list of users to the model with the name "users"
  - The HTML page can access this data using the name "users"

Similar patterns repeat for `jobs`, `applications`, `hired-candidates`, `interview-scheduled`, and `recruiters`. Each:
1. Checks if the requested page matches
2. Fetches data from the database using the repository
3. Adds the data to the model with a meaningful name

### Line 114: Return Statement
```java
return "admin";
```
- Returns the string `"admin"`, which tells Spring to show the `admin.html` template file

---

## Method 3: upload (File Upload) (Lines 117-137)

```java
@RequestMapping(path = "/admin/upload/users", method = RequestMethod.POST)
public ModelAndView upload(@RequestParam(\"users\") MultipartFile file, ModelAndView modelView,
        RedirectAttributes redirectAttributes) throws FileException {
```

**Annotations & Parameters:**
- **`@RequestMapping(path = "/admin/upload/users", method = RequestMethod.POST)`** - Handles POST requests to `/admin/upload/users`
  - POST is used when submitting forms (different from GET which just views)
- **`@RequestParam(\"users\") MultipartFile file`** - Gets a file that was uploaded
  - `@RequestParam` means: "Get this from the form submission"
  - `MultipartFile` is a file object that contains the uploaded file's data
- **`RedirectAttributes redirectAttributes`** - Used to pass messages that will show up on the next page
- **`throws FileException`** - If something goes wrong, this method throws (raises) a `FileException` error

### Lines 120-131: File Handling Logic
```java
if (file != null) {
    String fileName = new Date().getTime() + \"_\" + file.getOriginalFilename();
    Path path = Paths.get(new File(filePropertyConfig.getFilePath() + File.separator + fileName).toURI());
    try {
        Path outputPath = Files.write(path, file.getBytes());
        if (outputPath != null) {
            boolean result = userService.uploadUsers(path.toAbsolutePath().toString());
            if (result) {
                redirectAttributes.addFlashAttribute(\"message\", \"Users created successfully\");
            }
            modelView.setViewName(\"redirect:/admin/users\");
        }
    } catch (IOException io) {
        throw new FileException(\"Error while upload users\");
    }
}
```

Breaking it down:

- **`if (file != null)`** - Checks if a file was actually provided
- **`String fileName = new Date().getTime() + \"_\" + file.getOriginalFilename();`**
  - **`new Date().getTime()`** - Gets the current time in milliseconds (this creates a unique name)
  - The `+` symbol combines strings together
  - **`file.getOriginalFilename()`** - Dot calls the method to get the file's original name
  - Result: A unique filename combining timestamp and original filename (e.g., `1234567890_users.xlsx`)

- **`Path path = Paths.get(...)`**
  - **`Paths.get()`** - Creates a path (location) where the file will be saved
  - **`filePropertyConfig.getFilePath()`** - Dot calls method to get the upload directory from configuration
  - **`File.separator`** - Adds the correct separator (/ or \) depending on operating system
  - All these dots are chained method calls that build the full file path

- **`Files.write(path, file.getBytes())`**
  - **`Files.write()`** - Writes the file to disk
  - **`file.getBytes()`** - Dot calls method to convert the uploaded file into bytes (data)
  - Saves the file at the specified path

- **`if (outputPath != null)`** - Checks if the file was successfully written
- **`userService.uploadUsers(path.toAbsolutePath().toString())`**
  - **`path.toAbsolutePath()`** - Dot calls method to get the full path
  - **`.toString()`** - Another dot. Converts the path to a string
  - Calls the service to process the uploaded users file

- **`if (result)`** - Checks if uploading users was successful
- **`redirectAttributes.addFlashAttribute(\"message\", ...)`** - Dot calls method to add a success message
  - This message will show on the next page

- **`catch (IOException io)`** - If an IO (input/output) error occurs:
  - **`throw new FileException(...)`** - Throws a custom error message

### Line 136: Return
```java
return modelView;
```
- Returns the ModelAndView object back to Spring

---

## Method 4: upload (User Reset) (Lines 139-144)

```java
@RequestMapping(path = \"/admin/user/reset\", method = RequestMethod.POST)
public ModelAndView upload(@RequestParam(\"user\") int userId, ModelAndView modelView) throws FileException {
    userService.resetUser(userId);
    modelView.setViewName(\"redirect:/admin\");
    return modelView;
}
```

**Note:** This is another method also named `upload` (Java allows this - called "overloading" - as long as the parameters are different).

- **`@RequestParam(\"user\") int userId`** - Gets a user ID from the form submission
- **`userService.resetUser(userId)`** - Dot calls the service method to reset the user's password/data
- **`modelView.setViewName(\"redirect:/admin\")`** - Redirects back to admin home
- **`return modelView`** - Returns the response

---

## Method 5: staffService (Lines 146-162)

```java
@RequestMapping(path = \"/admin/staff/{operation}\", method = RequestMethod.POST)
public ModelAndView staffService(@RequestParam(name = \"staffname\", required = false) String name,
        @RequestParam(name = \"field\", required = false) String field,
        @RequestParam(name = \"technology\", required = false) String technology,
        @RequestParam(name = \"staffid\", required = false) String id, @PathVariable(\"operation\") String operation,
        ModelAndView modelView, RedirectAttributes redirectAttributes) throws FileException {
```

**Parameters:**
- **`@RequestParam(name = \"staffname\", required = false) String name`**
  - Gets the staff name from form
  - **`required = false`** means this field is optional (not required)
- Similar for `field`, `technology`, and `id`
- **`@PathVariable(\"operation\") String operation`** - Gets the operation type from URL (like `/admin/staff/add` or `/admin/staff/edit`)

### Lines 152-159: Conditional Logic
```java
if (operation.contentEquals(\"add\")) {
    staffService.addNewStaff(name, field, technology);
} else if (operation.contains(\"edit\")) {
    boolean isUpdated = staffService.editStaff(Integer.parseInt(id), name, field, technology);
    if (isUpdated) {
        redirectAttributes.addFlashAttribute(\"message\", \"Staff detail updated\");
    }
}
```

- **`operation.contentEquals(\"add\")`** - Checks if operation is "add"
  - **`staffService.addNewStaff(name, field, technology)`** - Dot calls method to add new staff
- **`operation.contains(\"edit\")`** - Checks if operation contains the word "edit"
  - **`Integer.parseInt(id)`** - Converts the string ID to an integer number
  - **`staffService.editStaff(...)`** - Dot calls method to update staff
  - **`if (isUpdated)`** - If update was successful, add a message

### Line 160-161: Redirect
```java
modelView.setViewName(\"redirect:/admin/staff\");
return modelView;
```
- Redirects back to the staff management page

---

## Method 6: courseService (Lines 164-173)

```java
@RequestMapping(path = \"/admin/course/add\", method = RequestMethod.POST)
public ModelAndView courseService(@RequestParam(name = \"coursename\", required = false) String name,
        @RequestParam(name = \"field\", required = false) String field,
        @RequestParam(name = \"staff\", required = false) Integer staff,
        @RequestParam(name = \"support\", required = false) List<Integer> support, ModelAndView modelView)
        throws FileException {
    courseService.addNewCourse(name, field, staff, support);
    modelView.setViewName(\"redirect:/admin/course\");
    return modelView;
}
```

**Parameters:**
- **`List<Integer> support`** - A list of integer IDs for supporting staff
  - **`List<...>`** means a collection of multiple items

**Logic:**
- **`courseService.addNewCourse(name, field, staff, support)`** - Dot calls method to add a new course with all the parameters
- Redirects back to course page

---

## Method 7: assignmentService (Lines 175-183)

```java
@RequestMapping(path = \"/admin/assignment/create\", method = RequestMethod.POST)
public ModelAndView assignmentService(@ModelAttribute AssignmentTemplate assignment, ModelAndView modelView)
        throws FileException {
    if (assignment != null) {
        assignmentService.createAssignment(assignment);
    }
    modelView.setViewName(\"redirect:/admin/assignment\");
    return modelView;
}
```

**Parameters:**
- **`@ModelAttribute AssignmentTemplate assignment`**
  - **`@ModelAttribute`** means Spring will automatically fill in the `assignment` object from form data
  - `AssignmentTemplate` is a class that holds assignment information

**Logic:**
- **`if (assignment != null)`** - Checks if assignment data was provided
- **`assignmentService.createAssignment(assignment)`** - Dot calls method to save the assignment
- Redirects back to assignment page

---

## Method 8: createJob (Lines 185-211)

```java
@RequestMapping(path = \"/admin/job/create\", method = RequestMethod.POST)
public ModelAndView createJob(
        @RequestParam String jobTitle,
        @RequestParam String location,
        @RequestParam String jobType,
        @RequestParam String experienceRequired,
        @RequestParam String jobDescription,
        @RequestParam String requiredSkills,
        ModelAndView modelView) {
```

**Parameters:**
- Multiple `@RequestParam` annotations for job details
- Each one gets a value from the form submission

### Lines 194-206: Creating and Saving Job
```java
String username = SecurityContextHolder.getContext().getAuthentication().getName();
UserDetail userDetail = userDetailRepository.findByEmailEquals(username);

JobPosting job = new JobPosting();
job.setJobTitle(jobTitle);
job.setLocation(location);
job.setJobType(jobType);
job.setExperienceRequired(experienceRequired);
job.setJobDescription(jobDescription);
job.setRequiredSkills(requiredSkills);
job.setPostedOn(new java.sql.Timestamp(System.currentTimeMillis()));
job.setIsActive(true);
job.setUserDetail(userDetail);

jobPostingRepository.save(job);
```

- Lines 194-195: Gets the currently logged-in user (same as explained before)
- **`JobPosting job = new JobPosting();`** - Creates a new job object
- **`job.setJobTitle(jobTitle)`** - Dot calls a setter method to set the job title
  - **`set...`** methods are used to assign values to object properties
- Similar for all other fields (location, jobType, etc.)
- **`new java.sql.Timestamp(System.currentTimeMillis())`** - Creates a timestamp of right now
- **`job.setIsActive(true)`** - Sets the job as active (not deleted)
- **`job.setUserDetail(userDetail)`** - Associates this job with the user who created it
- **`jobPostingRepository.save(job)`** - Dot calls method to save the job to the database

### Lines 209-210: Redirect
```java
modelView.setViewName(\"redirect:/admin/jobs\");
return modelView;
```

---

## Method 9: toggleJobStatus (Lines 213-222)

```java
@RequestMapping(path = \"/admin/job/toggle/{id}\", method = RequestMethod.POST)
public ModelAndView toggleJobStatus(@PathVariable Integer id, ModelAndView modelView) {
    JobPosting job = jobPostingRepository.findById(id).orElse(null);
    if (job != null) {
        job.setIsActive(!job.getIsActive());
        jobPostingRepository.save(job);
    }
    modelView.setViewName(\"redirect:/admin/jobs\");
    return modelView;
}
```

- **`@PathVariable Integer id`** - Gets the job ID from the URL
- **`jobPostingRepository.findById(id).orElse(null)`**
  - **`findById(id)`** - Dot calls method to find a job by its ID
  - **`.orElse(null)`** - Another dot. If not found, use `null` instead
- **`if (job != null)`** - Checks if job was found
- **`job.setIsActive(!job.getIsActive())`**
  - **`job.getIsActive()`** - Dot calls method to get the current active status
  - **`!`** symbol means "opposite of" (if true, becomes false; if false, becomes true)
  - This toggles (switches) the active status
- **`jobPostingRepository.save(job)`** - Saves the updated job

---

## Method 10: updateApplicationStatus (Lines 224-234)

```java
@GetMapping(\"/admin/application/status/{id}\")
public String updateApplicationStatus(@PathVariable Integer id, @RequestParam(required = false) String status) {
    if (status != null && !status.isEmpty()) {
        Application application = applicationRepository.findById(id).orElse(null);
        if (application != null) {
            application.setStatus(status);
            applicationRepository.save(application);
        }
    }
    return \"redirect:/admin/applications\";
}
```

**Note:** This method returns a `String` (just the view name) instead of `ModelAndView`.

- **`@GetMapping(...)`** - Shorthand for `@RequestMapping` with GET method
- **`@RequestParam(required = false) String status`** - Optional status parameter
- **`if (status != null && !status.isEmpty())`**
  - **`&&`** means "AND" - both conditions must be true
  - **`!status.isEmpty()`** means "status is NOT empty"
- **`Application application = applicationRepository.findById(id).orElse(null);`** - Finds the application
- **`application.setStatus(status)`** - Sets the new status
- **`applicationRepository.save(application)`** - Saves it
- **`return \"redirect:/admin/applications\";`** - Returns redirect URL as a string

---

## Method 11: createRecruiter (Lines 236-250)

```java
@RequestMapping(path = \"/admin/recruiter/create\", method = RequestMethod.POST)
public ModelAndView createRecruiter(
        @RequestParam String username,
        @RequestParam String email,
        ModelAndView modelView,
        RedirectAttributes redirectAttributes) {
    try {
        userService.createRecruiter(username, email);
        redirectAttributes.addFlashAttribute(\"message\", \"Recruiter created successfully\");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute(\"error\", \"Failed to create recruiter: \" + e.getMessage());
    }
    modelView.setViewName(\"redirect:/admin/recruiters\");
    return modelView;
}
```

**Error Handling with Try-Catch:**
- **`try { ... }`** - Try to run this code
  - **`userService.createRecruiter(username, email)`** - Attempts to create a recruiter
  - **`.addFlashAttribute("message", ...)`** - Adds a success message
- **`catch (Exception e) { ... }`** - If an error (Exception) occurs:
  - **`e.getMessage()`** - Dot calls method to get the error message
  - **`redirectAttributes.addFlashAttribute("error", ...)`** - Adds an error message
  - The `+` combines strings (concatenation)

---

## Method 12: toggleRecruiterStatus (Lines 252-261)

```java
@RequestMapping(path = \"/admin/recruiter/toggle/{id}\", method = RequestMethod.POST)
public ModelAndView toggleRecruiterStatus(@PathVariable Integer id, ModelAndView modelView) {
    UserDetail recruiter = userDetailRepository.findById(id).orElse(null);
    if (recruiter != null) {
        recruiter.setIsLocked(!recruiter.getIsLocked());
        userDetailRepository.save(recruiter);
    }
    modelView.setViewName(\"redirect:/admin/recruiters\");
    return modelView;
}
```

Same pattern as `toggleJobStatus`, but for recruiters. Toggles whether a recruiter is locked.

---

## Method 13: scheduleInterview (Lines 263-294)

```java
@PostMapping(\"/admin/interview/schedule/{applicationId}\")
public ModelAndView scheduleInterview(
        @PathVariable Integer applicationId,
        @RequestParam String interviewDate,
        @RequestParam String interviewTime,
        @RequestParam(required = false) String interviewerName,
        @RequestParam(required = false) String interviewLocation,
        ModelAndView modelView,
        RedirectAttributes redirectAttributes) {
```

**Note:** `@PostMapping` is shorthand for POST method requests.

### Lines 272-284: Processing Interview Details
```java
try {
    Application application = applicationRepository.findById(applicationId).orElse(null);
    if (application != null) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(\"yyyy-MM-dd\");
        SimpleDateFormat timeFormat = new SimpleDateFormat(\"HH:mm\");

        application.setInterviewDate(new java.sql.Date(dateFormat.parse(interviewDate).getTime()));
        application.setInterviewTime(new Time(timeFormat.parse(interviewTime).getTime()));
        application.setInterviewerName(interviewerName);
        application.setInterviewLocation(interviewLocation);
        application.setInterviewScheduledOn(new java.sql.Timestamp(System.currentTimeMillis()));

        applicationRepository.save(application);
        redirectAttributes.addFlashAttribute(\"message\", \"Interview scheduled successfully\");
    } else {
        redirectAttributes.addFlashAttribute(\"error\", \"Application not found\");
    }
} catch (Exception e) {
    redirectAttributes.addFlashAttribute(\"error\", \"Error scheduling interview: \" + e.getMessage());
}
```

- **`SimpleDateFormat dateFormat = new SimpleDateFormat(\"yyyy-MM-dd\");`**
  - Creates a date formatter object that understands dates in "yyyy-MM-dd" format (like 2024-12-22)
  - **`new`** keyword creates a new object
- **`dateFormat.parse(interviewDate)`** - Dot calls method to convert the string date into a Date object
- **`.getTime()`** - Dot calls method to get the time in milliseconds
- **`new java.sql.Date(...)`** - Creates a database-friendly date object
- **`new Time(...)`** - Creates a database-friendly time object
- **`.setInterviewScheduledOn(new java.sql.Timestamp(System.currentTimeMillis()))`**
  - **`System.currentTimeMillis()`** - Gets the current time in milliseconds
  - **`new java.sql.Timestamp(...)`** - Creates a timestamp object
- **`applicationRepository.save(application)`** - Saves the updated application

### Lines 285-294: Response Handling
```java
redirectAttributes.addFlashAttribute(\"message\", \"Interview scheduled successfully\");
} else {
    redirectAttributes.addFlashAttribute(\"error\", \"Application not found\");
}
} catch (Exception e) {
    redirectAttributes.addFlashAttribute(\"error\", \"Error scheduling interview: \" + e.getMessage());
}
modelAndView.setViewName(\"redirect:/admin/interview-scheduled\");
return modelView;
```

- If interview scheduling succeeds, add success message
- If application not found, add error message
- If any exception occurs, add error with the exception message
- Redirect to interview-scheduled page

---

## Method 14: updateInterview (Lines 296-326)

```java
@PostMapping(\"/admin/interview/update/{applicationId}\")
public ModelAndView updateInterview(
        @PathVariable Integer applicationId,
        @RequestParam String interviewDate,
        @RequestParam String interviewTime,
        @RequestParam(required = false) String interviewerName,
        @RequestParam(required = false) String interviewLocation,
        ModelAndView modelView,
        RedirectAttributes redirectAttributes) {
```

This is very similar to `scheduleInterview` method. The difference is:

### Lines 305-316
```java
try {
    Application application = applicationRepository.findById(applicationId).orElse(null);
    if (application != null) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(\"yyyy-MM-dd\");
        SimpleDateFormat timeFormat = new SimpleDateFormat(\"HH:mm\");

        application.setInterviewDate(new java.sql.Date(dateFormat.parse(interviewDate).getTime()));
        application.setInterviewTime(new Time(timeFormat.parse(interviewTime).getTime()));
        application.setInterviewerName(interviewerName);
        application.setInterviewLocation(interviewLocation);

        applicationRepository.save(application);
        redirectAttributes.addFlashAttribute(\"message\", \"Interview updated successfully\");
```

**Main difference:**
- **Does NOT set `InterviewScheduledOn` timestamp** (unlike schedule method)
- Message says "updated" instead of "scheduled"
- This means it's modifying an existing interview, not creating a new one

Rest of the error handling is the same.

---

## Method 15: cancelInterview (Lines 328-352)

```java
@PostMapping(\"/admin/interview/cancel/{applicationId}\")
public ModelAndView cancelInterview(
        @PathVariable Integer applicationId,
        ModelAndView modelView,
        RedirectAttributes redirectAttributes) {
    try {
        Application application = applicationRepository.findById(applicationId).orElse(null);
        if (application != null) {
            application.setInterviewDate(null);
            application.setInterviewTime(null);
            application.setInterviewerName(null);
            application.setInterviewLocation(null);
            application.setInterviewScheduledOn(null);

            applicationRepository.save(application);
            redirectAttributes.addFlashAttribute(\"message\", \"Interview cancelled successfully\");
        } else {
            redirectAttributes.addFlashAttribute(\"error\", \"Application not found\");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute(\"error\", \"Error cancelling interview: \" + e.getMessage());
    }
    modelAndView.setViewName(\"redirect:/admin/interview-scheduled\");
    return modelView;
}
```

- Finds the application by ID
- Sets all interview-related fields to `null` (empty/no value)
  - **`application.setInterviewDate(null)`** - Removes the interview date
  - All interview details are cleared
- Saves the application with cleared interview info
- Shows success message
- Has error handling for failures

---

## Method 16: deleteApplication (Lines 354-372)

```java
@PostMapping(\"/admin/application/delete/{applicationId}\")
public ModelAndView deleteApplication(
        @PathVariable Integer applicationId,
        ModelAndView modelView,
        RedirectAttributes redirectAttributes) {
    try {
        Application application = applicationRepository.findById(applicationId).orElse(null);
        if (application != null) {
            applicationRepository.deleteById(applicationId);
            redirectAttributes.addFlashAttribute(\"message\", \"Application deleted successfully\");
        } else {
            redirectAttributes.addFlashAttribute(\"error\", \"Application not found\");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute(\"error\", \"Error deleting application: \" + e.getMessage());
    }
    modelAndView.setViewName(\"redirect:/admin/applications\");
    return modelView;
}
```

- Finds the application by ID
- **`applicationRepository.deleteById(applicationId)`** - Dot calls method to delete the application from database
- Shows success message if deleted
- Error handling if application not found or if deletion fails
- Redirects to applications page

---

## Class Closing (Line 374)
```java
}
```
- This closing brace ends the `AdminController` class

---

## Summary of Key Concepts

### The Dot (.) Symbol
Every dot you see is a **method call** or **field access**:
- **`object.method()`** - Calling a method on an object
- **`object.field`** - Accessing a property of an object
- **`.chain().more().dots()`** - Multiple operations chained together (called "method chaining")

### Common Patterns in This Controller

1. **Get Data**: Get the logged-in user
2. **Fetch from DB**: Use repositories to get data
3. **Process**: Perform business logic using services
4. **Save**: Use repositories to save changes
5. **Respond**: Redirect or return a view name
6. **Error Handling**: Use try-catch to handle exceptions

### Request Handling Flow
```
User Request → @RequestMapping identifies the method →
Method gets parameters → Process data →
Save changes to database →
Redirect/Return view name → User sees result
```