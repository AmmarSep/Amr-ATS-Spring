# Complete Services Layer Explanation - Spring ATS

This document provides comprehensive explanations of all 12 services in the `com.spring.getready.services` package.

---

# 1. AssignmentService.java - Assignment Management

**Purpose:** Manages academic assignments - creating, checking submissions

**Key Methods:**

### createAssignment(AssignmentTemplate)
```java
public boolean createAssignment(AssignmentTemplate assignment) throws FileException
```
- Creates a new assignment from admin
- Handles optional reference file upload
- **Steps:**
  1. If reference file exists: Save file, create UploadFile record
  2. Get course from database
  3. Create AssignmentDetail object
  4. Set name, description, deadline, course
  5. Save to database
  6. Return true if successful

**File Handling:**
```java
String fileName = new Date().getTime() + "_" + assignment.getReference().getOriginalFilename();
Path path = Paths.get(...);
Path outputPath = Files.write(path, assignment.getReference().getBytes());
```
- Creates unique filename with timestamp
- Writes file bytes to disk
- Saves metadata to database

**Return Value:** `true` if assignment created, `false` if failed

### checkPendingAssignment(String uuid)
```java
public Map<String, Object> checkPendingAssignment(String uuid)
```
- Checks pending assignments for a user
- Returns a Map with two keys:
  - **"submissions"** - List of assignments user already submitted
  - **"assignments"** - List of pending assignments to submit

**Logic:**
1. Find user by UUID
2. Get all submissions by user
3. If user has submissions:
   - Find all assignments except those already submitted
4. If no submissions:
   - Show all active assignments

**Return Value:** Map with submissions and pending assignments

---

# 2. CourseService.java - Course Management

**Purpose:** Manages courses - creating courses with staff assignments

**Key Methods:**

### addNewCourse(String name, String field, Integer staffid, List<Integer> supportStaff)
```java
public boolean addNewCourse(String name, String field, Integer staffid, List<Integer> supportStaff)
```
- Creates a new course
- Assigns primary staff member
- Assigns support staff members

**Steps:**
1. Find primary staff by ID
   - If staffid is null, don't create course
2. Create CourseList object
3. Set name, field, primary staff
4. Add support staff members
   - Loop through supportStaff list
   - Find each staff by ID
   - Add course to their courseLists2 (many-to-many relationship)
5. Save course to database

**Data Model:**
```
Course
├── name ("Java Programming")
├── field ("Computer Science")
├── primaryStaff (John Doe)
└── supportStaff (Jane Smith, Bob Jones)
```

**Return Value:** `true` if course created, `false` if failed

---

# 3. ProfileService.java - User Profile Management

**Purpose:** Manages user profile information - personal details and photos

**Key Methods:**

### updateProfile(ProfileTemplate profileTemplate, UserDetail userDetail)
```java
public boolean updateProfile(ProfileTemplate profileTemplate, UserDetail userDetail)
```
- Updates user profile
- Optionally uploads profile picture

**Steps:**
1. Find existing ProfileInfo by user
2. Update fields:
   - About user
   - Address
   - Date of birth
   - Gender
   - Hometown
   - Religion
3. If profile picture uploaded:
   - Create unique filename
   - Write file to disk
   - Create UploadFile record
   - Link UploadFile to ProfileInfo
4. Save ProfileInfo to database

**Example Profile Data:**
```
ProfileInfo
├── aboutUser ("Software developer with 5 years experience")
├── addressUser ("123 Main St, Chennai")
├── dateOfBirth ("1992-03-15")
├── gender ("M")
├── hometown ("Chennai")
├── religion ("Hindu")
└── uploadFile (Profile picture)
```

**Return Value:** `true` if updated, `false` if failed

### getProfileDetails(String uuid)
```java
@SuppressWarnings("deprecation")
public ProfileInfo getProfileDetails(String uuid)
```
- Gets profile for a user
- **If profile doesn't exist:** Creates default profile

**Default Profile Values:**
```
aboutUser: "Describe yourself"
addressUser: "Address here"
dateOfBirth: 1991-02-01 (Note: Using deprecated Date constructor)
gender: "M"
hometown: "Chennai"
religion: "Hindu"
uploadFile: null
```

**Return Value:** ProfileInfo object (existing or newly created)

---

# 4. RelationService.java - Family Relations Management

**Purpose:** Manages family information - parents and siblings

**Key Methods:**

### getFamilyDetails(String uuid)
```java
public Map<String, Object> getFamilyDetails(String uuid)
```
- Gets all family information for a user
- Returns Map with two keys:
  - **"parents"** - FamilyDetail object
  - **"siblings"** - List of SiblingsDetail objects

**Flow:**
1. Find user by UUID
2. If user found:
   - Get family detail (parents)
   - Get siblings list
3. If user not found:
   - Put null for both parents and siblings

### getSiblingsDetails(FamilyDetail familyDetail)
```java
public List<SiblingsDetail> getSiblingsDetails(FamilyDetail familyDetail)
```
- Helper method to get all siblings for a family
- Returns empty list if familyDetail is null

### addFamilyDetails(ParentsTemplate parent, String uuid)
```java
public boolean addFamilyDetails(ParentsTemplate parent, String uuid)
```
- Adds parent information

**Steps:**
1. Find user
2. Create FamilyDetail object
3. Set father/mother names and occupations
4. Save FamilyDetail
5. Link FamilyDetail to user
6. Save user

**FamilyDetail Structure:**
```
FamilyDetail
├── fatherName ("Rajesh Kumar")
├── fatherOccupation ("Engineer")
├── motherName ("Priya Kumar")
└── motherOccupation ("Teacher")
```

### addSiblingDetails(ParentsTemplate parents, String uuid)
```java
public boolean addSiblingDetails(ParentsTemplate parents, String uuid)
```
- Adds a sibling record

**Steps:**
1. Find user
2. Create SiblingsDetail
3. Set sibling name and occupation
4. Link to family detail
5. Save sibling

### deleteSiblings(Integer siblingId, String uuid)
```java
public boolean deleteSiblings(Integer siblingId, String uuid)
```
- Deletes a sibling record
- Verifies user owns the record (via uuid check)

---

# 5. StaffService.java - Staff/Teacher Management

**Purpose:** Manages staff members (teachers/instructors)

**Key Methods:**

### addNewStaff(String name, String field, String technology)
```java
public boolean addNewStaff(String name, String field, String technology)
```
- Creates a new staff member

**Steps:**
1. Create StaffDetail object
2. Set name, field, technology known
3. Save to database
4. Check if save succeeded

**Example Staff Data:**
```
StaffDetail
├── staffName ("Dr. Ramesh")
├── field ("Computer Science")
└── technologyKnown ("Java, Python, Spring Boot")
```

### editStaff(int id, String name, String field, String technology)
```java
public boolean editStaff(int id, String name, String field, String technology)
```
- Updates an existing staff member

**Steps:**
1. Find staff by ID
2. If found:
   - Update name, field, technology
   - Save updated staff
3. Return true if successful

---

# 6. SubmissionService.java - Assignment Submission

**Purpose:** Handles assignment submissions by students

**Key Methods:**

### uploadSubmission(Integer assignmentId, UploadFile uploadFile, UserDetail userDetail)
```java
public boolean uploadSubmission(Integer assignmentId, UploadFile uploadFile, UserDetail userDetail)
```
- Records an assignment submission

**Steps:**
1. Find assignment by ID
2. Create SubmissionDetail object
3. Set:
   - Assignment reference
   - Upload file reference
   - User reference
   - Submission timestamp
4. Save to database
5. Return true if successful

**SubmissionDetail Object:**
```
SubmissionDetail
├── assignmentId (reference)
├── uploadFile (the submitted file)
├── userDetail (who submitted)
└── submittedOn (2024-12-22 14:30:45)
```

**Return Value:** `true` if submission saved, `false` if failed

---

# 7. AIResumeScreeningService.java - AI Resume Analysis

**Purpose:** Analyzes resumes using AI algorithms for recruitment

**Key Methods:**

### analyzeResume(String resumeText, String requiredSkills)
```java
public Map<String, Object> analyzeResume(String resumeText, String requiredSkills)
```
- Analyzes if resume matches job requirements
- Returns scoring and matched skills

**Algorithm:**
1. Convert resume to lowercase for case-insensitive matching
2. Split required skills by comma
3. Loop through each skill:
   - Check if skill appears in resume
   - If found, add to matchedSkills list
   - Increment matchCount
4. Calculate score:
   - Score = (matchCount * 100.0) / totalSkills
   - Example: 3 matches out of 5 skills = 60%
5. Round score to 2 decimal places

**Return Object:**
```java
{
  "score": 85.0,                           // Percentage match
  "matchedSkills": "java, spring, sql",    // Skills found
  "totalSkills": 5,                        // Total required
  "matchedCount": 3                        // Number found
}
```

**Example:**
```
Resume Text: "I have 5 years of Java experience,
             Spring Boot expertise, and SQL knowledge"

Required Skills: "Java, Spring Boot, SQL, Python, Docker"

Analysis:
- Java: FOUND ✓
- Spring Boot: FOUND ✓
- SQL: FOUND ✓
- Python: NOT FOUND ✗
- Docker: NOT FOUND ✗

Result:
- matchCount: 3
- score: 60% (3/5 * 100)
- matchedSkills: "java, spring boot, sql"
```

### extractKeywords(String text)
```java
public String extractKeywords(String text)
```
- Extracts important keywords from text
- Filters out common stop words (the, is, at, which, etc.)
- Keeps words longer than 3 characters
- Returns max 20 keywords

**Example:**
```
Input: "The Senior Java Developer must have experience
        with Spring Boot and PostgreSQL"

Processing:
1. Convert to lowercase
2. Split by word boundaries
3. Filter: length > 3 AND not in stopWords
4. Limit to 20
5. Join with commas

Output: "senior, java, developer, experience, spring,
         boot, postgresql"
```

### calculateExperienceScore(String resumeText)
```java
public int calculateExperienceScore(String resumeText)
```
- Scores experience level based on keywords

**Scoring:**
- Contains "years" or "experience": +20
- Contains "project" or "developed": +15
- Contains "team" or "lead": +10
- Contains "managed" or "coordinated": +10
- Maximum score: 50

**Example:**
```
Resume with: "5 years experience, developed projects,
             led a team"

Score: 20 + 15 + 10 = 45/50
```

---

# 8. UserService.java - User Management

**Purpose:** Manages user accounts, passwords, and bulk user import

**Key Methods:**

### uploadUsers(String path)
```java
public boolean uploadUsers(String path)
```
- Bulk imports users from JSON file
- Creates user accounts from file data

**Process:**
1. Read JSON file from path
2. Parse JSON into List<UserTemplate>
3. For each user in list:
   - Encode password (default)
   - Generate UUID from email
   - Find user group by ID
   - Create UserDetail object
   - Add to newUsers list
4. Save all users to database at once

**JSON Format Expected:**
```json
[
  {
    "username": "john_doe",
    "email": "john@ats.com",
    "group": 2
  },
  {
    "username": "jane_smith",
    "email": "jane@ats.com",
    "group": 2
  }
]
```

**Password Handling:**
```java
String password = passwordEncoder.encode(filePropertyConfig.getDefaultPassword());
// Encodes default password from config (usually "Ats@ABC")
// Using BCrypt algorithm
```

**UUID Generation:**
```java
UUID uuid = UUID.nameUUIDFromBytes(user.getEmail().getBytes("utf-8"));
// Creates consistent UUID from email
// Example: "john@ats.com" → "550e8400-e29b-41d4-a716-446655440000"
```

### resetUser(int userId)
```java
public boolean resetUser(int userId)
```
- Resets user password to default
- Cannot reset admin account

**Steps:**
1. Find user by ID
2. Check if user exists AND is not admin
3. If valid:
   - Encode default password
   - Set password on user
   - Unlock account (setIsLocked(false))
   - Save user
4. Return true if successful

**Security Check:**
```java
if (userDetail.get().getUserGroup().getGroupName()
    .toLowerCase().contentEquals("admin")) {
    // Don't allow admin reset
}
```

### createRecruiter(String username, String email)
```java
public UserDetail createRecruiter(String username, String email) throws Exception
```
- Creates a new recruiter account

**Validation:**
1. Check if email already exists
   - Throw exception if exists
2. Find RECRUITER user group (short code "REC")
   - Throw exception if not found

**Steps:**
1. Generate UUID from email
2. Encode default password
3. Create UserDetail object
4. Set all fields:
   - username, email, password
   - UUID, created timestamp
   - User group (RECRUITER)
   - isLocked = false
5. Save and return

**Return Value:** Saved UserDetail object

---

# 9. RecruitmentService.java - Recruitment Operations

**Purpose:** Manages recruitment workflow - jobs and applications with AI screening

**Key Methods:**

### getAllActiveJobs()
```java
public List<JobPosting> getAllActiveJobs()
```
- Gets all active (not closed) job postings
- Ordered by posted date (newest first)

**Database Query:**
```
findByIsActiveTrueOrderByPostedOnDesc()
```

**Return:** List of JobPosting objects

### submitApplication(Application application, String resumeText)
```java
public Application submitApplication(Application application, String resumeText)
```
- Submits a job application with AI resume screening
- **This is the core AI integration point**

**Steps:**
1. Get the job posting from application
2. Get required skills from job
3. Call AI service:
   ```java
   Map<String, Object> aiResult = aiScreeningService.analyzeResume(
       resumeText,                      // Extracted resume text
       job.getRequiredSkills()          // e.g., "Java, Spring, SQL"
   );
   ```
4. Extract AI results:
   - Score: `(Double) aiResult.get("score")`
   - Matched skills: `(String) aiResult.get("matchedSkills")`
5. Set application properties:
   - aiScore (0-100%)
   - aiMatchKeywords (matched skills)
   - status = "Submitted"
   - appliedOn = current timestamp
6. Save application to database
7. Return saved application

**Example Flow:**
```
User applies for "Senior Java Developer"
    ↓
Application received with resume
    ↓
Resume text extracted: "5 years Java, Spring Boot, Oracle..."
    ↓
Job requirements: "Java, Spring Boot, SQL, Docker"
    ↓
AI Analysis:
    - Java: Found ✓
    - Spring Boot: Found ✓
    - SQL: Found ✓
    - Docker: Not Found ✗
    - Score: 75%
    - Matched Skills: "java, spring boot, sql"
    ↓
Application saved with AI results
    ↓
Recruiter can see 75% score when reviewing
```

### getApplicationsByJob(JobPosting jobPosting)
```java
public List<Application> getApplicationsByJob(JobPosting jobPosting)
```
- Gets all applications for a specific job
- Sorted by AI score (highest first)

**Database Query:**
```
findByJobPostingOrderByAiScoreDesc()
```

**Return:** List of Application objects (best candidates first)

### updateApplicationStatus(Integer applicationId, String status)
```java
public Application updateApplicationStatus(Integer applicationId, String status)
```
- Updates application status (Submitted → Interview → Hired)
- Returns updated application or null if not found

---

# 10. CustomAuthProvider.java - Authentication

**Purpose:** Custom Spring Security authentication provider for login

**Key Method:**

### authenticate(Authentication authentication)
```java
@Override
public Authentication authenticate(Authentication authentication) throws AuthenticationException
```
- Validates login credentials
- Assigns user roles based on email/group

**Steps:**

1. **Extract Credentials:**
   ```java
   String username = authentication.getName();          // Email
   String password = authentication.getCredentials().toString();
   ```

2. **Find User in Database:**
   ```java
   UserDetail userDetail = userDetailRepository.findByEmailEquals(username);
   ```

3. **Validate Password:**
   ```java
   if (userDetail != null && passwordEncoder.matches(password, userDetail.getPassword()))
   ```
   - Compares entered password with stored BCrypt hash
   - BCrypt automatically verifies correctly

4. **Assign Roles Based on Email/Group:**
   ```java
   // Special case: admin@spring.ats
   if (username.equals("admin@spring.ats")) {
       grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
   }
   // All @ats.com users are candidates
   else if (username.endsWith("@ats.com")) {
       grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_CANDIDATE"));
   }
   // Fallback to group-based roles
   else {
       String groupName = userDetail.getUserGroup().getGroupName();
       if (groupName.equals("ADMIN")) {
           grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
       } else if (groupName.equals("RECRUITER")) {
           grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_RECRUITER"));
       } else {
           grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_CANDIDATE"));
       }
   }
   ```

5. **Update Last Login:**
   ```java
   userDetail.setLastLoginOn(new Timestamp(new Date().getTime()));
   userDetailRepository.save(userDetail);
   ```

6. **Create Authenticated Token:**
   ```java
   return new UsernamePasswordAuthenticationToken(
       username,                    // Principal
       password,                    // Credentials
       grantedAuthorities          // Roles
   );
   ```

**Return Value:**
- **Valid credentials:** Authenticated token with roles
- **Invalid credentials:** `null` (Spring Security handles rejection)

**Role Assignment Rules:**
```
admin@spring.ats → ROLE_ADMIN (hardcoded special case)
other@ats.com → ROLE_CANDIDATE (domain-based)
group.getGroupName() → ROLE_* (group-based fallback)
```

### supports(Class<?> authentication)
```java
@Override
public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
}
```
- Tells Spring Security this provider handles UsernamePasswordAuthenticationToken
- Returns `true` to indicate this provider should be used

---

# 11. UploadFileService.java - File Management

**Purpose:** Handles file uploads and text extraction from various file types

**Key Methods:**

### uploadFile(String fileName, String fileOriginalName)
```java
public UploadFile uploadFile(String fileName, String fileOriginalName)
```
- Creates a file record in database

**Steps:**
1. Create UploadFile object
2. Set fileName (unique name on server)
3. Set fileOriginalName (original user filename)
4. Set isDeleted = false
5. Set uploadedOn = current timestamp
6. Save to database
7. Return saved object

**Example:**
```
fileName: "12345678_resume.pdf"      // Unique on server
fileOriginalName: "MyResume.pdf"     // User's original name
```

### saveFile(MultipartFile file, String username)
```java
public UploadFile saveFile(MultipartFile file, String username) throws IOException
```
- Saves uploaded file to disk AND database

**Steps:**
1. Extract file extension
   ```java
   String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
   // "MyResume.pdf" → ".pdf"
   ```

2. Generate unique filename
   ```java
   String savedFilename = UUID.randomUUID().toString() + extension;
   // Example: "550e8400-e29b-41d4-a716-446655440000.pdf"
   ```

3. Create upload directory if needed
   ```java
   Path uploadDir = Paths.get(uploadPath);
   if (!Files.exists(uploadDir)) {
       Files.createDirectories(uploadDir);
   }
   ```

4. Copy file to disk
   ```java
   Path filePath = uploadDir.resolve(savedFilename);
   Files.copy(file.getInputStream(), filePath);
   ```

5. Create database record
   ```java
   return uploadFile(savedFilename, originalFilename);
   ```

**Upload Path:**
```
From @Value("${file.upload-path}")
Usually: /uploads or /var/uploads
```

### extractTextFromFile(UploadFile uploadFile)
```java
public String extractTextFromFile(UploadFile uploadFile)
```
- Extracts text content from uploaded file
- Supports PDF, DOCX, TXT, others

**File Type Handling:**
```
.pdf  → "PDF text extraction placeholder - MyResume.pdf"
.docx → "DOC/DOCX text extraction placeholder - MyResume.docx"
.txt  → Reads file content using Files.readAllBytes()
other → "Unsupported file format"
```

**Error Handling:**
```java
if (exception occurs) {
    return "Error reading file - " + originalName;
}
```

**Note:** PDF and DOCX currently have placeholder implementations. Production would use:
- Apache PDFBox for PDF extraction
- Apache POI for DOCX extraction

---

## Service Architecture Overview

```
Controllers (Handle HTTP Requests)
    ↓
Services (Business Logic)
├── AcademicService
├── AssignmentService
├── CourseService
├── ProfileService
├── RelationService
├── StaffService
├── SubmissionService
├── AIResumeScreeningService
├── UserService
├── RecruitmentService
├── CustomAuthProvider
└── UploadFileService
    ↓
Repositories (Database Access)
    ↓
Database
```

---

## Common Service Patterns

### Pattern 1: CRUD Operations
```java
// Create
boolean create(Object data);

// Read
Object getById(Integer id);

// Update
boolean update(Integer id, Object data);

// Delete
boolean delete(Integer id);
```

### Pattern 2: User Context
```java
// Get user from UUID
UserDetail userDetail = userDetailRepository.findByUserUuidEquals(uuid);
if (userDetail != null) {
    // Proceed with user's data
}
```

### Pattern 3: Optional Handling
```java
Optional<Entity> result = repository.findById(id);
if (result.isPresent()) {
    Entity entity = result.get();
    // Safe to use
}
```

### Pattern 4: File Operations
```java
// Generate unique filename
String fileName = new Date().getTime() + "_" + original;

// Write to disk
Path path = Paths.get(uploadPath + File.separator + fileName);
Files.write(path, fileBytes);

// Save metadata
UploadFile record = new UploadFile();
record.setFileName(fileName);
record.setFileOriginalName(original);
repository.save(record);
```

---

## Summary of All 12 Services

| Service | Purpose | Key Methods |
|---------|---------|------------|
| **AcademicService** | Academic details | get, add, delete |
| **AssignmentService** | Assignments | create, checkPending |
| **CourseService** | Courses | addNewCourse |
| **ProfileService** | User profile | updateProfile, getProfileDetails |
| **RelationService** | Family info | getFamily, addFamily, addSibling, deleteSibling |
| **StaffService** | Staff management | addNewStaff, editStaff |
| **SubmissionService** | Assignment submissions | uploadSubmission |
| **AIResumeScreeningService** | Resume analysis | analyzeResume, extractKeywords, calculateExperienceScore |
| **UserService** | User management | uploadUsers, resetUser, createRecruiter |
| **RecruitmentService** | Recruitment | getAllActiveJobs, submitApplication, getApplicationsByJob |
| **CustomAuthProvider** | Authentication | authenticate, supports |
| **UploadFileService** | File handling | uploadFile, saveFile, extractTextFromFile |

All services follow Spring best practices:
- ✅ `@Service` annotation
- ✅ Dependency injection via `@Autowired`
- ✅ Clear separation of concerns
- ✅ Business logic encapsulation
- ✅ Reusability across controllers
