# Spring Boot - AI-Enabled Applicant Tracking System (ATS)
AI-Enhanced ATS application for Smart Recruitment

>This application streamlines recruitment processes using AI-powered resume screening and candidate management

ATS is a web-based recruitment management system that maintains three types of users (Admin, Recruiter, and Candidate). The system automates candidate screening using AI algorithms, manages job postings, tracks applications, and facilitates interview scheduling.

##### This application includes Spring Core, Spring Security, Spring Data JPA, Thymeleaf (Template Engine), Postgres (Database), Project Lombok, and AI-based Resume Screening

## Key Features
- **Job Posting Management** - Create and manage job openings with required skills and experience
- **AI-Based Resume Screening** - Automatic candidate ranking using keyword matching and scoring algorithms
- **Application Tracking** - End-to-end application status management (Submitted → Screening → Interview → Hired/Rejected)
- **Interview Scheduling** - Schedule and track candidate interviews
- **Candidate Database** - Searchable repository of all applicants
- **Recruitment Analytics** - Dashboard with insights on hiring pipeline and statistics

###### Steps to run the project:
  1. Clone this project into a local directory
  2. Open the project in any IDE (IntelliJ IDEA, Eclipse, STS, etc.)
  3. Update the required maven dependencies for this project
  4. **Database Setup:**
     - Create a PostgreSQL database named `spring-ats`
     - Run the migration scripts from **MIGRATION_GUIDE.md** to create recruitment tables
     - Restore **spring-ats.sql** using: `psql -U username -d spring-ats -f db/spring-ats.sql`
  5. **Configuration:**
     - Update postgres *username* and *password* in **application.properties**
     - Update *file.upload-path* for resume uploads (e.g., `/path/to/resume/uploads`)
  6. Find and run **GetreadyApplication.java** from your IDE
  7. Open [http://localhost:8080/ats](http://localhost:8080/ats) in the browser
  8. **Default Credentials:**
     - Admin: `admin@spring.ats` / `Admin@ABC`
     - Create Recruiter and Candidate accounts through admin panel

## User Roles
- **Admin** - Full system access, user management, system configuration
- **Recruiter** - Create job postings, review applications, schedule interviews, access AI screening results
- **Candidate** - Register, apply for jobs, upload resumes, track application status

## AI Screening Process
1. Candidate submits application with resume
2. System extracts text from resume
3. AI algorithm matches skills against job requirements
4. Calculates match score (0-100%)
5. Ranks candidates by score for recruiter review
6. Identifies matched keywords for quick assessment

## Technology Stack
- **Backend:** Spring Boot 2.2.0, Spring Security, Spring Data JPA
- **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript
- **Database:** PostgreSQL
- **AI/ML:** Custom keyword matching and scoring algorithms
- **Build Tool:** Maven
- **Java Version:** 1.8+

## Project Structure
```
src/main/java/com/spring/getready/
├── config/          # Security and application configuration
├── controller/      # MVC controllers (Admin, Recruitment, etc.)
├── model/           # JPA entities (JobPosting, Application, etc.)
├── repository/      # Data access layer
├── services/        # Business logic (AIResumeScreeningService, RecruitmentService)
└── interceptor/     # Authentication handlers
```

## Documentation
- See **MIGRATION_GUIDE.md** for database schema and migration details
- See **Synopsis.md** for project objectives and requirements
- See **SYNOPSIS OF PROJECT WORK.pdf** for complete project documentation

## Future Enhancements
- Advanced NLP-based resume parsing
- Email/SMS notifications
- Video interview integration
- Advanced analytics dashboard
- Integration with job boards (LinkedIn, Indeed)

---
**Project Type:** Academic Project - MCA/MSC  
**Institution:** Tamil Nadu Open University  
**Category:** Web-based AI Application for HR Management
