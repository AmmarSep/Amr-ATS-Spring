# UserService & RecruitmentService - Detailed Explanations

---

# Part 1: UserService.java

## Service Overview

**Purpose:** Manages user accounts - bulk import, password reset, recruiter creation

**Location:** `com.spring.getready.services`

---

## Class Declaration (Lines 27-28)

```java
@Service
public class UserService {
```

- **`@Service`** - Spring service component
- Contains user account management logic

---

## Field Declarations (Lines 30-40)

### UserDetailRepository
```java
@Autowired
private UserDetailRepository userDetailRepository;
```
- Database access for users

### UserGroupRepository
```java
@Autowired
private UserGroupRepository userGroupRepository;
```
- Database access for user groups/roles

### PasswordEncoder
```java
@Autowired
private PasswordEncoder passwordEncoder;
```
- Spring Security component for password encryption
- Uses BCrypt algorithm
- Never stores plain text passwords

### FilePropertyConfig
```java
@Autowired
private FilePropertyConfig filePropertyConfig;
```
- Configuration with default password setting

---

## Method 1: uploadUsers (Lines 42-83)

```java
public boolean uploadUsers(String path) {
    boolean result = false;
    Timestamp date = new Timestamp(new Date().getTime());
    File file = new File(path);
    if (file.exists()) {
        try {
            String users = new String(Files.readAllBytes(Paths.get(file.toURI())));
            ObjectMapper objectMapper = new ObjectMapper();
            List<UserTemplate> allUsersList = objectMapper.readValue(users,
                    new TypeReference<List<UserTemplate>>() {});
            List<UserDetail> newUsers = new ArrayList<UserDetail>();
            for (int i = 0; i < allUsersList.size(); i++) {
                UserTemplate user = allUsersList.get(i);
                String password = passwordEncoder.encode(filePropertyConfig.getDefaultPassword()).toString();
                UUID uuid = UUID.nameUUIDFromBytes(user.getEmail().getBytes("utf-8"));
                Optional<UserGroup> userGroup = userGroupRepository.findById(user.getGroup());
                if (userGroup.isPresent()) {
                    UserDetail newUser = new UserDetail();
                    newUser.setUsername(user.getUsername());
                    newUser.setPassword(password);
                    newUser.setEmail(user.getEmail());
                    newUser.setUserUuid(uuid.toString());
                    newUser.setCreatedOn(date);
                    newUser.setIsLocked(false);
                    newUser.setUserGroup(userGroup.get());
                    newUsers.add(newUser);
                }
            }
            if (newUsers.size() > 0) {
                List<UserDetail> savedUsers = userDetailRepository.saveAll(newUsers);
                if (savedUsers.size() > 0) {
                    result = true;
                }
            }
        } catch (IOException e) {
            System.out.println("IOException occurs");
            e.printStackTrace();
        }
    }
    return result;
}
```

### What It Does
- Bulk imports users from JSON file
- Creates user accounts in batch
- Sets up default passwords and roles

### Line-by-Line Breakdown

**Line 42:**
```java
public boolean uploadUsers(String path) {
```
- **`String path`** - File path to JSON file
  - Example: `/uploads/1703082345_users.json`
- **Returns:** `true` if any users created, `false` otherwise

**Line 43:**
```java
boolean result = false;
```
- Initialize to false (assume failure)
- Set to true only if users successfully saved

**Line 44:**
```java
Timestamp date = new Timestamp(new Date().getTime());
```
- **`new Date().getTime()`** - Gets current time in milliseconds
- **`new Timestamp(...)`** - Converts to SQL Timestamp
- Used for all users' createdOn timestamp
- Ensures all imported users have same creation time

**Line 45:**
```java
File file = new File(path);
```
- **`new File(path)`** - Creates File object pointing to the path
- Doesn't read file yet, just creates reference

**Line 46:**
```java
if (file.exists()) {
```
- **`.exists()`** - Dot calls method to check if file actually exists
- Only proceed if file found
- Prevents FileNotFoundException

**Line 48:**
```java
String users = new String(Files.readAllBytes(Paths.get(file.toURI())));
```

Breaking it down:
- **`file.toURI()`** - Dot calls method to convert to URI
- **`Paths.get(...)`** - Dot calls method to get file path
- **`Files.readAllBytes(...)`** - Dot calls method to read entire file as bytes
- **`new String(...)`** - Converts bytes to String
- **Result:** Entire file content as string

**Example:**
```
File content:
[
  {"username": "john", "email": "john@ats.com", "group": 2},
  {"username": "jane", "email": "jane@ats.com", "group": 2}
]

After reading: String containing above JSON
```

**Line 49:**
```java
ObjectMapper objectMapper = new ObjectMapper();
```
- **`ObjectMapper`** - Jackson library class for JSON processing
- **`new ObjectMapper()`** - Creates instance
- Used to parse JSON string into Java objects

**Line 50-51:**
```java
List<UserTemplate> allUsersList = objectMapper.readValue(users,
        new TypeReference<List<UserTemplate>>() {});
```

Breaking it down:
- **`objectMapper.readValue(...)`** - Dot calls method to parse JSON
  - **First parameter:** `users` - JSON string
  - **Second parameter:** `new TypeReference<List<UserTemplate>>()` - Target type
    - Tells Jackson what to deserialize into
    - Generic type information for the list
- **Result:** List of UserTemplate objects (deserialized from JSON)

**Why TypeReference?**
- Java generics are erased at runtime
- `readValue()` needs explicit type information
- Without it, Jackson wouldn't know it's a List<UserTemplate>

**Example Deserialization:**
```
JSON String:
"[{"username":"john","email":"john@ats.com","group":2}]"
    ↓
ObjectMapper.readValue()
    ↓
List<UserTemplate>:
[
  UserTemplate(username="john", email="john@ats.com", group=2)
]
```

**Line 53:**
```java
List<UserDetail> newUsers = new ArrayList<UserDetail>();
```
- Creates empty list to hold UserDetail objects
- Will be populated in the loop
- These are the actual entities to save to database

**Line 54:**
```java
for (int i = 0; i < allUsersList.size(); i++) {
```
- **Traditional for loop** (not enhanced for)
  - **`int i = 0`** - Start at 0
  - **`i < allUsersList.size()`** - Continue while i is less than list size
  - **`i++`** - Increment by 1
- **Reason for traditional loop?** Possibly legacy code, could use enhanced for

**Line 55:**
```java
UserTemplate user = allUsersList.get(i);
```
- **`.get(i)`** - Dot calls method to get item at index i
- Gets current user from JSON

**Line 56:**
```java
String password = passwordEncoder.encode(filePropertyConfig.getDefaultPassword()).toString();
```

Breaking it down:
- **`filePropertyConfig.getDefaultPassword()`** - Dot calls method to get default password
  - From application.properties: usually "Ats@ABC"
- **`passwordEncoder.encode(...)`** - Dot calls method to encrypt
  - Uses BCrypt algorithm
  - **BCrypt:** One-way hashing, can't decrypt
  - Example: "Ats@ABC" → "$2a$10$abcd1234..." (different each time)
- **`.toString()`** - Another dot, converts to string
  - BCrypt returns PasswordEncodedResult, needs conversion

**Password Security:**
```
Plain: "Ats@ABC"
Encoded: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWDeWMGEq32LaJ2e"
        (different every time, even with same input)

Verification:
- User enters: "Ats@ABC"
- System encodes: "$2a$10/different/hash/again"
- System compares with stored using BCrypt verify()
- BCrypt knows they're the same password
```

**Line 57:**
```java
UUID uuid = UUID.nameUUIDFromBytes(user.getEmail().getBytes("utf-8"));
```

Breaking it down:
- **`user.getEmail()`** - Gets user's email
- **`.getBytes("utf-8")`** - Dot calls method to convert to bytes using UTF-8 encoding
  - Example: "john@ats.com" → [106, 111, 104, 110, 64, ...]
- **`UUID.nameUUIDFromBytes(...)`** - Dot calls static method to generate UUID
  - **UUID** - Universally Unique Identifier
  - Example: "550e8400-e29b-41d4-a716-446655440000"
  - **Deterministic:** Same email always produces same UUID
  - **Collision-free:** Different emails produce different UUIDs

**Why nameUUIDFromBytes?**
- Creates consistent UUID from email
- If same email uploaded twice, same UUID
- Useful for duplicate detection

**UUID Example:**
```
Email: "john@ats.com"
UTF-8 bytes: [106, 111, 104, 110, ...]
UUID: "a3bb189e-8bf9-3888-9912-ace4e6543002" (consistent)

Different email:
Email: "jane@ats.com"
UUID: "550e8400-e29b-41d4-a716-446655440000" (completely different)
```

**Line 58:**
```java
Optional<UserGroup> userGroup = userGroupRepository.findById(user.getGroup());
```
- Finds the UserGroup (role) by ID
- **`user.getGroup()`** - Gets group ID from UserTemplate
- Optional might be empty if group ID doesn't exist

**Line 59:**
```java
if (userGroup.isPresent()) {
```
- Only create user if group exists
- Can't create user without valid group

**Lines 60-68: Create UserDetail Object**
```java
UserDetail newUser = new UserDetail();
newUser.setUsername(user.getUsername());
newUser.setPassword(password);
newUser.setEmail(user.getEmail());
newUser.setUserUuid(uuid.toString());
newUser.setCreatedOn(date);
newUser.setIsLocked(false);
newUser.setUserGroup(userGroup.get());
```

Building the user object:
- **`new UserDetail()`** - Create new user entity
- **`.setUsername(...)`** - Set username
- **`.setPassword(password)`** - Set encrypted password
- **`.setEmail(...)`** - Set email
- **`.setUserUuid(uuid.toString())`** - Set UUID
  - **`.toString()`** - Convert UUID object to string
- **`.setCreatedOn(date)`** - Set creation timestamp
- **`.setIsLocked(false)`** - User not locked initially
- **`.setUserGroup(userGroup.get())`** - Link to group/role

**Line 69:**
```java
newUsers.add(newUser);
```
- Add the created user to the list

**Line 71:**
```java
if (newUsers.size() > 0) {
```
- Check if any users were successfully created
- Don't save if list is empty

**Line 72:**
```java
List<UserDetail> savedUsers = userDetailRepository.saveAll(newUsers);
```
- **`saveAll(...)`** - Dot calls method to save all users in one database operation
  - More efficient than saving one by one
- **Returns:** List of saved users (with IDs assigned)

**Line 73:**
```java
if (savedUsers.size() > 0) {
```
- Check if any users were actually saved
- Size > 0 confirms success

**Line 74:**
```java
result = true;
```
- Set result to true

**Line 77-79:**
```java
catch (IOException e) {
    System.out.println("IOException occurs");
    e.printStackTrace();
}
```
- Catches file reading errors
- Prints to console and stack trace
- Continues without crashing

**Line 82:**
```java
return result;
```
- Returns true if successful, false otherwise

---

## Method 2: resetUser (Lines 85-98)

```java
public boolean resetUser(int userId) {
    boolean result = false;
    String password = passwordEncoder.encode(filePropertyConfig.getDefaultPassword()).toString();
    Optional<UserDetail> userDetail = userDetailRepository.findById(userId);
    if (userDetail.isPresent()
            && !userDetail.get().getUserGroup().getGroupName().toLowerCase().contentEquals("admin")) {
        UserDetail user = userDetail.get();
        user.setPassword(password);
        user.setIsLocked(false);
        userDetailRepository.save(user);
        result = true;
    }
    return result;
}
```

### What It Does
- Resets user password to default
- Cannot reset admin password (security)
- Unlocks user account

### Line-by-Line Breakdown

**Line 85:**
```java
public boolean resetUser(int userId) {
```
- **`int userId`** - User ID to reset

**Line 87:**
```java
String password = passwordEncoder.encode(filePropertyConfig.getDefaultPassword()).toString();
```
- Same password encoding as uploadUsers

**Line 88:**
```java
Optional<UserDetail> userDetail = userDetailRepository.findById(userId);
```
- Find user by ID

**Lines 89-90:**
```java
if (userDetail.isPresent()
        && !userDetail.get().getUserGroup().getGroupName().toLowerCase().contentEquals("admin")) {
```

Breaking it down:
- **`userDetail.isPresent()`** - User exists
- **`&&`** - AND
- **`!userDetail.get().getUserGroup().getGroupName().toLowerCase().contentEquals("admin")`**
  - **`.get()`** - Extract UserDetail from Optional
  - **`.getUserGroup()`** - Get the user's group
  - **`.getGroupName()`** - Get group name
  - **`.toLowerCase()`** - Convert to lowercase
  - **`.contentEquals("admin")`** - Check if equals "admin"
  - **`!`** - NOT operator (true if NOT admin)

**Security Check:**
```
if (isPresent && isNotAdmin) {
    // Allow reset
} else {
    // Prevent reset
}
```

**Lines 91-94: Update User**
```java
UserDetail user = userDetail.get();
user.setPassword(password);
user.setIsLocked(false);
userDetailRepository.save(user);
```
- Extract user from Optional
- Set new password
- Unlock account
- Save to database

**Line 95:**
```java
result = true;
```
- Mark as successful

**Line 97:**
```java
return result;
```
- Return success/failure

---

## Method 3: createRecruiter (Lines 100-124)

```java
public UserDetail createRecruiter(String username, String email) throws Exception {
    UserDetail existing = userDetailRepository.findByEmailEquals(email);
    if (existing != null) {
        throw new Exception("Email already exists");
    }

    UserGroup recruiterGroup = userGroupRepository.findByShortGroupEquals("REC");
    if (recruiterGroup == null) {
        throw new Exception("Recruiter group not found");
    }

    String password = passwordEncoder.encode(filePropertyConfig.getDefaultPassword());
    UUID uuid = UUID.nameUUIDFromBytes(email.getBytes("utf-8"));

    UserDetail recruiter = new UserDetail();
    recruiter.setUsername(username);
    recruiter.setEmail(email);
    recruiter.setPassword(password);
    recruiter.setUserUuid(uuid.toString());
    recruiter.setCreatedOn(new Timestamp(new Date().getTime()));
    recruiter.setIsLocked(false);
    recruiter.setUserGroup(recruiterGroup);

    return userDetailRepository.save(recruiter);
}
```

### What It Does
- Creates a new recruiter account
- Validates email doesn't exist
- Validates recruiter group exists
- Returns created recruiter

### Line-by-Line Breakdown

**Line 100:**
```java
public UserDetail createRecruiter(String username, String email) throws Exception {
```
- **Returns:** UserDetail object (the created recruiter)
- **`throws Exception`** - Can throw exceptions (caller must handle)

**Line 101:**
```java
UserDetail existing = userDetailRepository.findByEmailEquals(email);
```
- Check if email already in use

**Line 102:**
```java
if (existing != null) {
```
- If user with this email exists

**Line 103:**
```java
throw new Exception("Email already exists");
```
- **`throw new Exception(...)`** - Throws exception
  - Stops method execution
  - Caller must catch with try-catch
  - **Message:** "Email already exists"

**Line 106:**
```java
UserGroup recruiterGroup = userGroupRepository.findByShortGroupEquals("REC");
```
- Find recruiter group by short code "REC"
- **Why short code?** More concise, unique identifier

**Line 107:**
```java
if (recruiterGroup == null) {
```
- If group not found

**Line 108:**
```java
throw new Exception("Recruiter group not found");
```
- Throw exception

**Line 111:**
```java
String password = passwordEncoder.encode(filePropertyConfig.getDefaultPassword());
```
- Encode default password

**Line 112:**
```java
UUID uuid = UUID.nameUUIDFromBytes(email.getBytes("utf-8"));
```
- Generate UUID from email

**Lines 114-123: Create Recruiter**
```java
UserDetail recruiter = new UserDetail();
recruiter.setUsername(username);
recruiter.setEmail(email);
recruiter.setPassword(password);
recruiter.setUserUuid(uuid.toString());
recruiter.setCreatedOn(new Timestamp(new Date().getTime()));
recruiter.setIsLocked(false);
recruiter.setUserGroup(recruiterGroup);
```
- Create and configure recruiter object
- Similar to uploadUsers but with validation

**Line 125:**
```java
return userDetailRepository.save(recruiter);
```
- Save to database and return

---

---

# Part 2: RecruitmentService.java

## Service Overview

**Purpose:** Manages recruitment workflow - jobs, applications, and AI screening

**Location:** `com.spring.getready.services`

---

## Class Declaration (Lines 12-13)

```java
@Service
public class RecruitmentService {
```

---

## Field Declarations (Lines 15-22)

### JobPostingRepository
```java
@Autowired
private JobPostingRepository jobPostingRepository;
```
- Access to job postings

### ApplicationRepository
```java
@Autowired
private ApplicationRepository applicationRepository;
```
- Access to applications

### AIResumeScreeningService
```java
@Autowired
private AIResumeScreeningService aiScreeningService;
```
- **Critical dependency** - Uses AI for resume analysis

---

## Method 1: getAllActiveJobs (Lines 24-26)

```java
public List<JobPosting> getAllActiveJobs() {
    return jobPostingRepository.findByIsActiveTrueOrderByPostedOnDesc();
}
```

### What It Does
- Gets all active job postings
- Ordered by posting date (newest first)

### Breakdown

**Custom Repository Query:**
```
findByIsActiveTrueOrderByPostedOnDesc()
```

Breaking down the method name:
- **`findBy`** - Fetch records where
- **`IsActive`** - isActive field
- **`True`** - equals true (job is active)
- **`OrderBy`** - Sort by
- **`PostedOn`** - postedOn field
- **`Desc`** - Descending order (newest first)

**Equivalent SQL:**
```sql
SELECT * FROM job_postings
WHERE is_active = true
ORDER BY posted_on DESC
```

**Return:** List of JobPosting objects

---

## Method 2: saveJobPosting (Lines 28-30)

```java
public JobPosting saveJobPosting(JobPosting jobPosting) {
    return jobPostingRepository.save(jobPosting);
}
```

- Simple wrapper around save
- Allows business logic layer to handle saves

---

## Method 3: submitApplication (Lines 32-46)

```java
public Application submitApplication(Application application, String resumeText) {
    JobPosting job = application.getJobPosting();

    Map<String, Object> aiResult = aiScreeningService.analyzeResume(
        resumeText,
        job.getRequiredSkills()
    );

    application.setAiScore((Double) aiResult.get("score"));
    application.setAiMatchKeywords((String) aiResult.get("matchedSkills"));
    application.setStatus("Submitted");
    application.setAppliedOn(new java.sql.Timestamp(System.currentTimeMillis()));

    return applicationRepository.save(application);
}
```

### What It Does
- Submits application with AI resume analysis
- **Core integration point** between recruitment and AI

### Line-by-Line Breakdown

**Line 33:**
```java
JobPosting job = application.getJobPosting();
```
- **`.getJobPosting()`** - Dot calls method to get the job
- Gets required skills from job later

**Lines 35-38: Call AI Service**
```java
Map<String, Object> aiResult = aiScreeningService.analyzeResume(
    resumeText,
    job.getRequiredSkills()
);
```

Breaking it down:
- **`aiScreeningService.analyzeResume(...)`** - Dot calls AI service
  - **Parameter 1:** `resumeText` - Extracted resume content
  - **Parameter 2:** `job.getRequiredSkills()` - Required skills like "Java, Spring, SQL"
- **Returns:** Map with score, matchedSkills, etc.

**Example:**
```
Job requires: "Java, Spring Boot, SQL, Docker"
Resume has: "5 years Java and Spring Boot development"

AI Analysis:
{
  "score": 75.0,
  "matchedSkills": "java, spring boot",
  "totalSkills": 4,
  "matchedCount": 2
}
```

**Line 40:**
```java
application.setAiScore((Double) aiResult.get("score"));
```

Breaking it down:
- **`aiResult.get("score")`** - Gets score from map
  - Map returns Object type (generic)
- **`(Double)`** - Cast to Double type
  - **Cast:** Convert from Object to Double
  - Without cast, can't call Double methods
- **`.setAiScore(...)`** - Dot calls method to set score on application

**Why Cast?**
```
Map is Map<String, Object>
Object is generic (could be any type)
Score could be Double, Integer, etc.
Explicit cast tells compiler "I know it's a Double"
```

**Line 41:**
```java
application.setAiMatchKeywords((String) aiResult.get("matchedSkills"));
```
- Gets matched skills from map
- Casts to String
- Sets on application

**Example Values:**
```
Before: application.aiScore = null
        application.aiMatchKeywords = null

After:  application.aiScore = 75.0
        application.aiMatchKeywords = "java, spring boot"
```

**Line 42:**
```java
application.setStatus("Submitted");
```
- Sets application status
- Indicates application was received

**Line 43:**
```java
application.setAppliedOn(new java.sql.Timestamp(System.currentTimeMillis()));
```

Breaking it down:
- **`System.currentTimeMillis()`** - Gets current time in milliseconds
- **`new java.sql.Timestamp(...)`** - Converts to SQL Timestamp
- **`.setAppliedOn(...)`** - Sets application timestamp
- Records when application was submitted

**Line 45:**
```java
return applicationRepository.save(application);
```
- Saves application with AI results to database
- Returns the saved application (now has ID)

**Complete AI Integration Flow:**
```
RecruitmentController.applyForJob()
    ↓
Uploads file → UploadFileService.saveFile()
Extracts text → UploadFileService.extractTextFromFile()
    ↓
RecruitmentService.submitApplication(application, resumeText)
    ↓
AIResumeScreeningService.analyzeResume(
    resumeText,
    job.getRequiredSkills()
)
    ↓
Returns: {score: 75.0, matchedSkills: "java, spring"}
    ↓
Sets on application:
- aiScore = 75.0
- aiMatchKeywords = "java, spring"
- status = "Submitted"
- appliedOn = 2024-12-22 14:30:45
    ↓
Saves application to database
    ↓
Recruiter sees 75% score when reviewing
```

---

## Method 4: getApplicationsByJob (Lines 48-50)

```java
public List<Application> getApplicationsByJob(JobPosting jobPosting) {
    return applicationRepository.findByJobPostingOrderByAiScoreDesc(jobPosting);
}
```

### What It Does
- Gets all applications for a job
- Sorted by AI score (best candidates first)

**Database Query:**
```
findByJobPostingOrderByAiScoreDesc(job)
```
- Find where jobPosting matches
- Order by aiScore descending (highest first)

**Benefit:** Recruiters see best-matched candidates first

---

## Method 5: updateApplicationStatus (Lines 52-59)

```java
public Application updateApplicationStatus(Integer applicationId, String status) {
    Application app = applicationRepository.findById(applicationId).orElse(null);
    if (app != null) {
        app.setStatus(status);
        return applicationRepository.save(app);
    }
    return null;
}
```

### What It Does
- Updates application status
- Used to change Submitted → Interview → Hired

**Status Progression:**
```
Submitted → Under Review → Interview → Hired/Rejected
```

**Breakdown:**
- Find application by ID
- If found, update status and save
- Return updated application or null if not found

---

## Integration Summary

```
User submits application
    ↓
RecruitmentController.applyForJob()
    ↓
UploadFileService (save & extract)
    ↓
RecruitmentService.submitApplication()
    ↓
AIResumeScreeningService.analyzeResume()
    ↓
Application saved with AI score
    ↓
Recruiter views applications
    ↓
Sorted by AI score (best first)
    ↓
Can update status (Interview, Hire, etc.)
```

---

## Summary

### UserService
- **uploadUsers()** - Bulk import from JSON
- **resetUser()** - Reset password
- **createRecruiter()** - Create recruiter account

### RecruitmentService
- **getAllActiveJobs()** - List jobs
- **submitApplication()** - Process application with AI
- **getApplicationsByJob()** - List applications sorted by AI score
- **updateApplicationStatus()** - Update application status

**Key Integration:** RecruitmentService calls AIResumeScreeningService to analyze resumes automatically during submission.
