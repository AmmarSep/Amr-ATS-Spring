# RecruitmentController.java - Detailed Line-by-Line Explanation

## Class Declaration (Lines 21-23)

```java
@Controller
@RequestMapping(\"/recruitment\")
public class RecruitmentController {
```

- **`@Controller`** - Spring annotation marking this as a MVC Controller (returns HTML pages)

- **`@RequestMapping(\"/recruitment\")`** - Class-level mapping
  - All methods in this controller are prefixed with `/recruitment`
  - Example: `@GetMapping("/jobs")` becomes `/recruitment/jobs`
  - This groups related methods together

- **`public class RecruitmentController`** - A public class that handles job recruitment

**Purpose:** This controller manages the recruitment workflow - job listing, job details, job applications, and viewing applications for a job.

---

## Field Declarations (Lines 25-35)

### RecruitmentService (Lines 25-26)
```java
@Autowired
private RecruitmentService recruitmentService;
```
- Service containing recruitment business logic

### JobPostingRepository (Lines 28-29)
```java
@Autowired
private JobPostingRepository jobPostingRepository;
```
- Repository for accessing job postings from database

### UploadFileService (Lines 31-32)
```java
@Autowired
private UploadFileService uploadFileService;
```
- Service for handling file uploads and processing

### UserDetailRepository (Lines 34-35)
```java
@Autowired
private UserDetailRepository userDetailRepository;
```
- Repository for accessing user information from database

---

## Method 1: listJobs (Lines 37-46)

```java
@GetMapping(\"/jobs\")
public String listJobs(Model model) {
```

### Annotation & Parameters

- **`@GetMapping(\"/jobs\")`** - Shorthand for `@RequestMapping("/recruitment/jobs")`
  - Handles GET requests to `/recruitment/jobs`

- **`public String listJobs(Model model)`** - Returns a String (view name)

- **`Model model`** - Object to hold data for HTML template

---

### Lines 39-40: Get Current User

```java
String username = SecurityContextHolder.getContext().getAuthentication().getName();
UserDetail userDetail = userDetailRepository.findByEmailEquals(username);
```

- Gets the currently logged-in user's email and full details
- Same pattern as other controllers

---

### Line 42: Get All Active Jobs

```java
List<JobPosting> jobs = recruitmentService.getAllActiveJobs();
```

- **`recruitmentService.getAllActiveJobs()`** - Dot calls method
  - Gets all active (not deleted/closed) job postings from database
  - Returns a List of JobPosting objects

- **`List<JobPosting> jobs`** - Stores the list in a variable

---

### Lines 43-44: Add Data to Model

```java
model.addAttribute(\"jobs\", jobs);
model.addAttribute(\"username\", userDetail.getUsername());
```

- **`model.addAttribute("jobs", jobs)`** - Dot calls method
  - Adds the jobs list to the model
  - HTML template can iterate through it with loop

- **`model.addAttribute("username", ...)`** - Adds username for display

---

### Line 45: Return View Name

```java
return \"recruitment/job-list\";
```

- Returns "recruitment/job-list"
- Spring looks for `recruitment/job-list.html` template file
- The HTML displays:
  - All active jobs in a list
  - User can click on a job to see details
  - User can click "Apply" to apply for the job

---

## Method 2: viewJob (Lines 48-56)

```java
@GetMapping(\"/job/{id}\")
public String viewJob(@PathVariable Integer id, Model model) {
```

### Annotation & Parameters

- **`@GetMapping(\"/job/{id}\")`** - Handles GET requests to `/recruitment/job/5`
  - **`{id}`** is a placeholder for the job ID

- **`@PathVariable Integer id`** - Gets the job ID from the URL
  - If URL is `/recruitment/job/5`, then `id = 5`

- **`Model model`** - Object to hold data for HTML

---

### Lines 50-52: Get Job or Redirect

```java
JobPosting job = jobPostingRepository.findById(id).orElse(null);
if (job == null) {
    return \"redirect:/recruitment/jobs\";
}
```

- **`jobPostingRepository.findById(id)`** - Dot calls method to find job by ID
  - **`.orElse(null)`** - If job not found, use null instead

- **`if (job == null)`** - Checks if job was found
  - If not found, redirect to jobs list
  - User can't view a non-existent job

---

### Lines 54-55: Add Job to Model and Return

```java
model.addAttribute(\"job\", job);
return \"recruitment/job-detail\";
```

- Adds the job object to model
- Returns "recruitment/job-detail"
- HTML displays:
  - Job title, description, requirements, location
  - Salary, job type, experience required
  - "Apply Now" button

---

## Method 3: showApplicationForm (Lines 58-66)

```java
@GetMapping(\"/apply/{id}\")
public String showApplicationForm(@PathVariable Integer id, Model model) {
```

### Annotation & Parameters

- **`@GetMapping(\"/apply/{id}\")`** - Handles GET requests to `/recruitment/apply/5`
  - Shows the application form for that job

- **`@PathVariable Integer id`** - Gets the job ID from URL

---

### Lines 60-62: Get Job or Redirect

```java
JobPosting job = jobPostingRepository.findById(id).orElse(null);
if (job == null) {
    return \"redirect:/recruitment/jobs\";
}
```

- Same as viewJob(): find job by ID, redirect if not found

---

### Lines 64-65: Add Job to Model and Return

```java
model.addAttribute(\"job\", job);
return \"recruitment/apply\";
```

- Adds job details to model
- Returns "recruitment/apply"
- HTML displays:
  - Job details (read-only)
  - File upload form for resume
  - Text area for optional notes
  - Submit button

---

## Method 4: applyForJob (Lines 68-124)

```java
@PostMapping(\"/apply\")
public String applyForJob(
        @RequestParam Integer jobRef,
        @RequestParam(\"resume\") MultipartFile resume,
        @RequestParam(required = false) String notes,
        Authentication auth,
        RedirectAttributes redirectAttributes) {
```

### Annotation & Parameters

- **`@PostMapping(\"/apply\")`** - Handles POST requests to `/recruitment/apply`
  - Form submission from apply.html

- **`@RequestParam Integer jobRef`** - Gets the job ID being applied for
  - Hidden form field with job ID

- **`@RequestParam("resume") MultipartFile resume`** - Gets uploaded resume file

- **`@RequestParam(required = false) String notes`** - Optional notes about the application

- **`Authentication auth`** - Spring Security provides current user authentication
  - Contains username of the applicant

- **`RedirectAttributes redirectAttributes`** - For passing messages to next page

---

### Lines 76-81: Log Application Details

```java
System.out.println(\"=== APPLICATION SUBMISSION START ===\");
System.out.println(\"Job ID: \" + jobRef);
System.out.println(\"User: \" + auth.getName());
System.out.println(\"File name: \" + (resume != null ? resume.getOriginalFilename() : \"NULL\"));
System.out.println(\"File size: \" + (resume != null ? resume.getSize() : \"NULL\"));
System.out.println(\"File empty: \" + (resume != null ? resume.isEmpty() : \"NULL\"));
```

**Debugging logs:** Print detailed information about the application

- **`auth.getName()`** - Dot calls method to get applicant's username
- **`resume.getOriginalFilename()`** - Gets filename user gave it
- **`resume.getSize()`** - Gets file size in bytes
- **`resume.isEmpty()`** - Checks if file is actually there
- **Ternary operator `? :`** - If resume exists, use its value, else use "NULL"

---

### Line 83: Start Try Block

```java
try {
```

- Handles potential errors during application submission

---

### Lines 85-88: Validate Resume File

```java
if (resume == null || resume.isEmpty()) {
    System.out.println(\"ERROR: Resume file is empty or null\");
    redirectAttributes.addFlashAttribute(\"error\", \"Please select a resume file.\");
    return \"redirect:/recruitment/apply/\" + jobRef;
}
```

**What this does:** Check if user actually uploaded a file

- **`resume == null || resume.isEmpty()`**
  - **`||`** = "OR" operator
  - Checks if file is null OR if file is empty

- If true, show error and redirect back to apply form
  - **`"redirect:/recruitment/apply/" + jobRef`** - Redirect URL with job ID
  - String concatenation with `+`

---

### Line 91-93: Save File

```java
System.out.println(\"Step 1: Saving file...\");
UploadFile uploadedResume = uploadFileService.saveFile(resume, auth.getName());
System.out.println(\"Step 1 completed - File ID: \" + uploadedResume.getFileId());
```

- **`uploadFileService.saveFile(resume, auth.getName())`** - Dot calls method
  - **Parameters:** the resume file and applicant's username
  - Saves file to disk and creates database record
  - Returns UploadFile object with file details

---

### Line 95-97: Extract Text from Resume

```java
System.out.println(\"Step 2: Extracting text...\");
String resumeText = uploadFileService.extractTextFromFile(uploadedResume);
System.out.println(\"Step 2 completed - Text length: \" + resumeText.length());
```

- **`uploadFileService.extractTextFromFile(uploadedResume)`** - Dot calls method
  - Extracts all text from the resume file (PDF, Word, etc.)
  - Converts to searchable text
  - Used for AI screening and resume analysis
  - Returns the extracted text as a String

---

### Lines 99-101: Find Candidate User

```java
System.out.println(\"Step 3: Finding candidate...\");
UserDetail candidate = userDetailRepository.findByEmailEquals(auth.getName());
System.out.println(\"Step 3 completed - Candidate ID: \" + (candidate != null ? candidate.getUserId() : \"NULL\"));
```

- **`userDetailRepository.findByEmailEquals(auth.getName())`** - Dot calls method
  - Finds the full user record for the applicant
  - **`auth.getName()`** returns the username/email

---

### Lines 103-108: Create Application Object

```java
System.out.println(\"Step 4: Creating application...\");
Application application = new Application();
application.setJobPosting(jobPostingRepository.findById(jobRef).orElse(null));
application.setCandidate(candidate);
application.setResume(uploadedResume);
application.setNotes(notes);
```

**What this does:** Build the application object

- **`new Application()`** - Creates a new application object
- **`application.setJobPosting(...)`** - Dot calls setter
  - Links this application to a job posting
  - Gets the job by ID from database

- **`application.setCandidate(candidate)`** - Links to the user applying
- **`application.setResume(uploadedResume)`** - Links to the uploaded resume file
- **`application.setNotes(notes)`** - Sets optional applicant notes

---

### Lines 110-112: Submit Application

```java
System.out.println(\"Step 5: Submitting application...\");
Application savedApp = recruitmentService.submitApplication(application, resumeText);
System.out.println(\"Step 5 completed - Application ID: \" + savedApp.getApplicationId());
```

- **`recruitmentService.submitApplication(application, resumeText)`** - Dot calls method
  - **Parameters:** the application object and extracted resume text
  - **What it does:**
    1. Saves application to database
    2. Runs AI resume screening on the resume text
    3. Calculates match score with job requirements
    4. Updates application with AI results
  - Returns the saved application with ID

---

### Lines 114-115: Success Response

```java
redirectAttributes.addFlashAttribute(\"success\", \"Application submitted successfully!\");
System.out.println(\"=== APPLICATION SUBMISSION SUCCESS ===\");
```

- Adds success message that shows on next page
- Logs completion

---

### Lines 116-121: Catch Exceptions

```java
} catch (Exception e) {
    System.out.println(\"ERROR in application submission: \" + e.getClass().getSimpleName() + \" - \" + e.getMessage());
    e.printStackTrace();
    redirectAttributes.addFlashAttribute(\"error\", \"Failed to submit application: \" + e.getMessage());
    return \"redirect:/recruitment/apply/\" + jobRef;
}
```

**If any error occurs:**

- **`e.getClass().getSimpleName()`** - Dot calls method to get error type
  - Returns exception name like "IOException", "SQLException"

- **`e.getMessage()`** - Dot calls method to get error message
- **`e.printStackTrace()`** - Prints full stack trace for debugging
- **`redirectAttributes.addFlashAttribute("error", ...)`** - Shows error to user
- **Redirects back to apply form** so user can try again

---

### Line 123: Return Success Redirect

```java
return \"redirect:/recruitment/jobs\";
```

- After successful application, redirect to jobs list
- User sees their application was accepted

---

## Method 5: viewApplications (Lines 126-137)

```java
@GetMapping(\"/job/{id}/applications\")
public String viewApplications(@PathVariable Integer id, Model model) {
```

### Annotation & Parameters

- **`@GetMapping(\"/job/{id}/applications\")`** - Handles GET requests to `/recruitment/job/5/applications`
  - Shows all applications for a specific job

- **`@PathVariable Integer id`** - Gets the job ID from URL

---

### Lines 128-131: Get Job or Redirect

```java
JobPosting job = jobPostingRepository.findById(id).orElse(null);
if (job == null) {
    return \"redirect:/recruitment/jobs\";
}
```

- Same pattern: find job by ID, redirect if not found

---

### Lines 133-136: Get Applications and Add to Model

```java
List<Application> applications = recruitmentService.getApplicationsByJob(job);
model.addAttribute(\"job\", job);
model.addAttribute(\"applications\", applications);
return \"recruitment/applications\";
```

- **`recruitmentService.getApplicationsByJob(job)`** - Dot calls method
  - Gets all applications for this job
  - Returns a list of Application objects

- Adds job and applications to model
- Returns "recruitment/applications"
- HTML displays:
  - Job details
  - Table of all applications for that job
  - Applicant name, application date, AI score
  - Links to view each application

---

## Application Submission Flow Diagram

```
1. USER VIEWS JOBS
   ↓
   GET /recruitment/jobs
   ↓
   listJobs() is called
   ↓
   Returns job-list.html with all jobs

2. USER CLICKS ON A JOB
   ↓
   GET /recruitment/job/5
   ↓
   viewJob() is called with id=5
   ↓
   Returns job-detail.html with job details

3. USER CLICKS "APPLY NOW"
   ↓
   GET /recruitment/apply/5
   ↓
   showApplicationForm() is called with id=5
   ↓
   Returns apply.html with form

4. USER FILLS FORM & UPLOADS RESUME
   ↓
   POST /recruitment/apply
   ↓
   applyForJob() is called
   ↓
   Step 1: Save resume file to disk and database
   ↓
   Step 2: Extract text from resume
   ↓
   Step 3: Find user record in database
   ↓
   Step 4: Create Application object
   ↓
   Step 5: Submit to service
      - Service runs AI screening
      - Calculates match score
      - Saves to database
   ↓
   Success message added
   ↓
   Redirects to /recruitment/jobs

5. RECRUITER VIEWS APPLICATIONS FOR A JOB
   ↓
   GET /recruitment/job/5/applications
   ↓
   viewApplications() is called with id=5
   ↓
   Returns applications.html
   ↓
   Shows all applications with AI scores
```

---

## AI Resume Screening Pipeline

**This happens inside `submitApplication(application, resumeText)`:**

```
1. Resume text extracted from PDF/Word
   ↓
2. Text sent to AIResumeScreeningService
   ↓
3. AI Algorithm:
   a. Extracts keywords from job requirements
   b. Counts keyword matches in resume
   c. Analyzes years of experience
   d. Scores qualifications
   ↓
4. Match score calculated (0-100%)
   ↓
5. Matched keywords identified
   ↓
6. Score and keywords saved to Application
   ↓
7. Recruiter can see AI score when reviewing
```

---

## Key Request/Response Examples

### Example 1: List All Jobs
```
Request:  GET /recruitment/jobs
Response: job-list.html with list of 5 active jobs
```

### Example 2: View Job Details
```
Request:  GET /recruitment/job/3
Response: job-detail.html with job #3 details
          - Title: Senior Java Developer
          - Requirements: Java, Spring Boot, SQL
          - Apply Now button
```

### Example 3: Apply for Job
```
Request:  GET /recruitment/apply/3
Response: apply.html with application form
          - Resume file upload
          - Optional notes textarea
          - Submit button

Request:  POST /recruitment/apply
          jobRef=3
          resume=myresume.pdf (uploaded file)
          notes="Looking forward to this opportunity"

Processing:
1. Save myresume.pdf to /uploads/1703082345_myresume.pdf
2. Create UploadFile record in database
3. Extract text from PDF
4. Find candidate user record
5. Create Application object
6. Call AIResumeScreeningService
   - AI reads resume text
   - Compares with "Java, Spring Boot, SQL"
   - Finds Java (match), Spring Boot (match), SQL (match)
   - Calculates 85% score
7. Save Application with AI score
8. Return success message
9. Redirect to /recruitment/jobs

Response: Redirects to /recruitment/jobs
Display:  Success message "Application submitted successfully!"
```

### Example 4: View All Applications for a Job
```
Request:  GET /recruitment/job/3/applications
Response: applications.html showing:
          - Job: Senior Java Developer
          - Table of applications:
            | Applicant | Date | Status | AI Score |
            | John Doe | 12/22/2024 | Pending | 85% |
            | Jane Smith | 12/21/2024 | Under Review | 92% |
            | Bob Jones | 12/20/2024 | Rejected | 45% |
```

---

## The Dot (.) Symbol in This File

- **Method calls:** `getAllActiveJobs()`, `findById()`, `extractTextFromFile()`
- **Chained calls:** `jobPostingRepository.findById(id).orElse(null)`
- **Getters/Setters:** `getFileId()`, `setJobPosting()`, `getName()`
- **Ternary operator:** `resume != null ? resume.getOriginalFilename() : "NULL"`

---

## Summary

**RecruitmentController's Job:**
- Display available job postings
- Show job details
- Display and process job applications
- Handle resume uploads
- Trigger AI resume screening
- Display applications with AI scores

**Five Methods:**
1. **listJobs()** - Shows all active jobs
2. **viewJob()** - Shows details of one job
3. **showApplicationForm()** - Shows application form for a job
4. **applyForJob()** - Processes application submission with file upload and AI screening
5. **viewApplications()** - Shows all applications for a job with AI scores

**Key Points:**
- Resume is extracted to text for AI screening
- Application object links job, candidate, and resume
- RecruitmentService handles AI screening and scoring
- Comprehensive error handling and logging
- Flash attributes for user feedback
- Path variables for job IDs
- File upload with validation

**AI Integration:**
- Resume text extracted automatically
- AI screening triggered during submission
- Match score calculated and saved
- Recruiters can see AI insights when reviewing applications
