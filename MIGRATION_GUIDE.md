# Migration Guide: Assignment Tracking to AI-Enabled ATS

## Database Schema Changes

### New Tables Required

```sql
-- Job Postings Table
CREATE TABLE job_postings (
    job_id SERIAL PRIMARY KEY,
    job_title VARCHAR(255) NOT NULL,
    job_description TEXT,
    required_skills TEXT,
    experience_required VARCHAR(100),
    location VARCHAR(255),
    job_type VARCHAR(50),
    posted_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    posted_by INTEGER REFERENCES user_details(user_id)
);

-- Applications Table
CREATE TABLE applications (
    application_id SERIAL PRIMARY KEY,
    job_ref INTEGER REFERENCES job_postings(job_id),
    candidate_ref INTEGER REFERENCES user_details(user_id),
    resume_ref INTEGER REFERENCES upload_files(file_id),
    applied_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Submitted',
    ai_score DECIMAL(5,2),
    ai_match_keywords TEXT,
    interview_scheduled_on TIMESTAMP,
    notes TEXT
);
```

### User Group Updates

Update user groups to support recruitment roles:
- **Admin** - Full system access
- **Recruiter** - Manage job postings, review applications
- **Candidate** - Apply for jobs, track application status

```sql
-- Update existing groups or add new ones
INSERT INTO user_groups (group_name, group_description) 
VALUES ('Recruiter', 'HR/Recruiter role for managing recruitment');

UPDATE user_groups SET group_description = 'Job Candidate' 
WHERE group_name = 'Student';
```

## Key Features Implemented

### 1. Job Posting Management
- Create, update, and manage job postings
- Set required skills, experience, and deadlines
- Activate/deactivate job postings

### 2. AI-Based Resume Screening
- Automatic keyword matching against job requirements
- Skill-based scoring algorithm
- Experience level detection
- Candidate ranking by AI score

### 3. Application Management
- Candidate application submission
- Resume upload and parsing
- Application status tracking (Submitted, Screening, Interview, Rejected, Hired)
- Interview scheduling

### 4. Recruitment Dashboard
- View all applications per job posting
- Sort candidates by AI score
- Track recruitment pipeline
- Generate recruitment reports

## AI Screening Algorithm

The system uses a simple but effective AI algorithm:

1. **Keyword Matching**: Extracts and matches skills from resume against job requirements
2. **Scoring**: Calculates match percentage (0-100%)
3. **Ranking**: Orders candidates by AI score for easy shortlisting
4. **Keyword Extraction**: Identifies key terms from resumes

## Configuration

Update `application.properties`:
```properties
# Resume upload path
file.upload-path=/path/to/resume/uploads

# AI Screening settings (optional)
ai.screening.enabled=true
ai.screening.min-score=50
```

## Next Steps

1. Run the SQL migration scripts to create new tables
2. Update user roles in the database
3. Configure file upload paths for resumes
4. Test job posting and application workflow
5. Customize AI screening parameters as needed

## Future Enhancements

- Advanced NLP-based resume parsing
- Email/SMS notifications for application status
- Video interview scheduling integration
- Advanced analytics and reporting
- Integration with external job boards
