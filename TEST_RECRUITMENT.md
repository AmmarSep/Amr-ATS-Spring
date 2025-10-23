# Test Recruitment Module

## ✅ Application Running: http://localhost:8080/ats

## Test Steps:

### 1. Login as Admin
- URL: http://localhost:8080/ats/login
- Email: `admin@spring.ats`
- Password: `Admin@ABC`

### 2. Create Recruiter User
- Go to Admin panel → Users
- Create user with role: **Recruiter**

### 3. Create Candidate User
- Create user with role: **Candidate**

### 4. Test as Candidate
- Logout and login as Candidate
- Go to: http://localhost:8080/ats/recruitment/jobs
- Click "Senior Java Developer"
- Click "Apply Now"
- Upload: `test-resumes/sample-resume-high-match.txt`
- Submit

### 5. Test as Recruiter
- Logout and login as Recruiter
- Go to: http://localhost:8080/ats/recruitment/job/1/applications
- View AI score: **100%**
- See matched keywords: java, spring boot, microservices, rest api, postgresql, docker, kubernetes

### 6. Test Multiple Resumes
Upload all 3 test resumes and verify ranking:
- High match: 100% (Rank 1)
- Medium match: 42.86% (Rank 2)
- Low match: 0% (Rank 3)

## Quick URLs:
- Jobs: http://localhost:8080/ats/recruitment/jobs
- Applications: http://localhost:8080/ats/recruitment/job/1/applications
