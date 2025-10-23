# Quick Start Guide - ATS Recruitment Module

## 🚀 Start Application

```bash
cd /Users/ammar.s.s/Code/spring-getready-placements
mvn spring-boot:run
```

Access: **http://localhost:8080/ats**

---

## 👥 User Roles & Access

| Role | Access | Endpoints |
|------|--------|-----------|
| **Admin** | Full system access | `/admin/**`, `/recruitment/**` |
| **Recruiter** | Manage recruitment | `/recruitment/applications/**`, `/recruitment/manage/**` |
| **Candidate** | Apply for jobs | `/recruitment/jobs`, `/recruitment/apply/**` |

---

## 📋 Recruitment Workflow

### For Candidates:

1. **Browse Jobs**
   - URL: `/recruitment/jobs`
   - View all active job postings

2. **View Job Details**
   - URL: `/recruitment/job/{id}`
   - See full job description and requirements

3. **Apply for Job**
   - URL: `/recruitment/apply/{id}`
   - Upload resume (PDF/DOC/DOCX)
   - Add optional cover letter
   - Submit application

4. **AI Screening**
   - System automatically analyzes resume
   - Matches skills against job requirements
   - Calculates AI score (0-100%)
   - Extracts matched keywords

### For Recruiters:

1. **View Applications**
   - URL: `/recruitment/job/{id}/applications`
   - See all candidates for a job
   - Sorted by AI score (highest first)

2. **Review Candidates**
   - View AI match score
   - See matched skills
   - Download resumes
   - Update application status

3. **Manage Recruitment**
   - Track application pipeline
   - Schedule interviews
   - Make hiring decisions

---

## 🧪 Test with Sample Resumes

Location: `test-resumes/`

### Quick Test:

```bash
# View test documentation
cat test-resumes/README.md

# Run test script
bash test-resumes/test-ai-screening.sh
```

### Test Resumes:

1. **High Match (100%)** - `sample-resume-high-match.txt`
   - All required skills present
   - Should rank 1st

2. **Medium Match (42.86%)** - `sample-resume-medium-match.txt`
   - Partial skill match
   - Should rank 2nd

3. **Low Match (0%)** - `sample-resume-low-match.txt`
   - No matching skills
   - Should rank 3rd

---

## 🔍 Verify Implementation

```bash
bash verify-implementation.sh
```

Checks:
- ✅ Database tables
- ✅ Thymeleaf views
- ✅ Java implementations
- ✅ Test resumes
- ✅ Security configuration

---

## 📊 Database

### Tables Created:

**job_postings**
- Stores job openings
- Required skills (comma-separated)
- Posted by recruiters

**applications**
- Candidate applications
- AI score (0-100)
- Matched keywords
- Application status

### Sample Data:

```sql
-- View sample job
SELECT * FROM job_postings WHERE job_title = 'Senior Java Developer';

-- View applications with AI scores
SELECT a.application_id, u.first_name, u.last_name, a.ai_score, a.ai_match_keywords
FROM applications a
JOIN user_details u ON a.candidate_ref = u.user_id
ORDER BY a.ai_score DESC;
```

---

## 🎯 Key Features

### AI Resume Screening
- Automatic skill matching
- Keyword extraction
- Score calculation
- Candidate ranking

### Job Management
- Create job postings
- Set required skills
- Track applications
- Manage deadlines

### Application Tracking
- Status workflow: Submitted → Screening → Interview → Hired/Rejected
- Interview scheduling
- Notes and feedback

### Security
- Role-based access control
- Secure file uploads
- Session management

---

## 📁 Project Structure

```
src/main/
├── java/com/spring/getready/
│   ├── config/
│   │   └── SecurityConfig.java          # Role permissions
│   ├── controller/
│   │   └── RecruitmentController.java   # Recruitment endpoints
│   ├── model/
│   │   ├── JobPosting.java              # Job entity
│   │   └── Application.java             # Application entity
│   ├── repository/
│   │   ├── JobPostingRepository.java
│   │   └── ApplicationRepository.java
│   └── services/
│       ├── RecruitmentService.java      # Business logic
│       ├── AIResumeScreeningService.java # AI algorithm
│       └── UploadFileService.java       # File handling
└── resources/
    └── templates/recruitment/
        ├── job-list.html                # Job listings
        ├── job-detail.html              # Job details
        ├── apply.html                   # Application form
        └── applications.html            # Recruiter view
```

---

## 🐛 Troubleshooting

### Database Connection Error
```bash
# Check PostgreSQL is running
psql -U ammar.s.s -d spring-ats -c "SELECT 1;"

# Verify credentials in application.properties
cat src/main/resources/application.properties | grep datasource
```

### File Upload Error
```bash
# Check upload directory exists
ls -la /Users/ammar.s.s/Documents/ATS-Uploads

# Create if missing
mkdir -p /Users/ammar.s.s/Documents/ATS-Uploads
```

### Custom Error Page
- If you see "Custom error page", check application logs
- Verify user has proper role assigned
- Check SecurityConfig role mappings

---

## 📚 Documentation

- **README.md** - Project overview
- **IMPLEMENTATION_SUMMARY.md** - Detailed implementation notes
- **NEXT_STEPS_COMPLETED.md** - Completion report
- **MIGRATION_GUIDE.md** - Database schema details
- **test-resumes/README.md** - Testing instructions

---

## 🎓 Default Credentials

**Admin:**
- Email: `admin@spring.ats`
- Password: `Admin@ABC`

**Note:** Create Recruiter and Candidate accounts through admin panel

---

## ✅ Next Steps Completed

1. ✅ Database migration executed
2. ✅ Thymeleaf views implemented
3. ✅ SecurityConfig updated with role permissions
4. ✅ AI screening tested with sample resumes

**Status:** Ready for recruitment workflow testing!
