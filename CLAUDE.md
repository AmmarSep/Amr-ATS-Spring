# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Application Overview

This is a **Spring Boot AI-enabled Applicant Tracking System (ATS)** for recruitment management. The application features automated resume screening using AI algorithms, role-based access control, and comprehensive recruitment workflow management.

**Key Technologies:** Spring Boot 2.2.0, Spring Security, JPA/Hibernate, PostgreSQL, Thymeleaf, Lombok, Java 11

## Essential Commands

### Build & Run (IDE Recommended)
```bash
# The application has pre-existing Lombok annotation processing issues
# IDE-based execution is recommended:
# 1. Enable Lombok plugin in IntelliJ/Eclipse
# 2. Run GetreadyApplication.java from IDE
# 3. Access: http://localhost:8080/ats

# Alternative Maven commands (may fail due to Lombok issues):
mvn clean install -DskipTests  # Build without tests
mvn spring-boot:run           # Run application
./mvnw spring-boot:run        # Using Maven wrapper
```

### Testing
```bash
# Standard testing
mvn test                                    # Run JUnit tests
./mvnw test                                # Using Maven wrapper

# Functional testing
bash test-resumes/test-ai-screening.sh     # Test AI resume screening
bash verify-implementation.sh             # Verify implementation
bash RUN_APPLICATION.sh                   # Build and run with checks

# Database verification
psql -U ammar.s.s -d spring-ats -c "SELECT * FROM job_postings;"
```

### Database Setup
```bash
# Create PostgreSQL database
psql -U postgres -c "CREATE DATABASE spring-ats;"

# Restore database (if needed)
psql -U postgres -d spring-ats -f db/spring-ats.sql

# Update credentials in: src/main/resources/application.properties
```

## Architecture Overview

### MVC Pattern Implementation
- **Controllers**: Role-based endpoints (`AdminController`, `RecruitmentController`, `LoginController`)
- **Services**: Business logic with AI integration (`AIResumeScreeningService`, `RecruitmentService`)
- **Repositories**: JPA data access layer with custom queries
- **Models**: Comprehensive entities for recruitment workflow (`JobPosting`, `Application`, `UserDetail`)

### AI Resume Screening (Core Feature)
The application's unique selling point is automated resume analysis:
- **Algorithm**: Skills matching + keyword extraction + experience scoring
- **Integration**: Automatic scoring during application submission
- **Output**: 0-100% match score with identified keywords
- **Location**: `AIResumeScreeningService.analyzeResume()` method

### Security Configuration
- **Authentication**: Custom provider with domain-based role assignment
- **Authorization**: Role hierarchy (ADMIN → RECRUITER → CANDIDATE → USER)
- **Special**: `@ats.com` users automatically get CANDIDATE role
- **Passwords**: BCrypt encoding, default: `Ats@ABC`

### File Upload System
- **Configuration**: 3MB per file, 10MB per request
- **Storage**: UUID-based filenames in configurable directory
- **Text Extraction**: Automatic resume content parsing for AI analysis

## User Roles & Access Patterns

### Admin (`admin@ats.com` / `Admin@ABC`)
- User management (create recruiters/candidates)
- System-wide analytics and configuration
- Full access to all recruitment data

### Recruiter
- Job posting creation and management
- Application review with AI scoring insights
- Interview scheduling and status updates

### Candidate  
- Profile creation and job browsing
- Resume upload and application submission
- Application status tracking

## Database Schema Key Points

### Core Tables
- `job_postings`: Jobs with required skills and metadata
- `applications`: Applications with AI scoring fields (`ai_score`, `ai_match_keywords`)
- `user_details`: Users with role-based permissions
- `upload_files`: File management with text extraction storage

### Important Relationships
- Jobs → Applications (one-to-many)
- Users → Applications (one-to-many with role-based filtering)
- Applications → Files (one-to-one for resume uploads)

## Development Guidelines

### Working with AI Components
- **Resume analysis**: Triggered automatically during application submission
- **Score calculation**: Located in `AIResumeScreeningService.calculateMatchScore()`
- **Keyword extraction**: Uses simple text matching algorithm
- **Testing**: Use sample resumes in `test-resumes/` directory

### Authentication Flow
1. Login through `/ats/login`
2. Domain-based role assignment in `CustomAuthProvider`
3. Role-based URL access in `SecurityConfig`
4. Session management via Spring Security

### File Upload Workflow
1. Multipart upload to `UploadFileService`
2. UUID filename generation for security
3. Text extraction for AI analysis
4. Database persistence with metadata

## Common Development Tasks

### Adding New Job Fields
1. Update `JobPosting` entity
2. Modify `job-posting.html` template
3. Update `RecruitmentService` validation
4. Add database migration script

### Extending AI Algorithm
1. Modify `AIResumeScreeningService.analyzeResume()`
2. Update score calculation logic
3. Test with sample resumes in `test-resumes/`
4. Update AI scoring display in templates

### Role Management
1. Add roles to `UserGroup` enum
2. Update `SecurityConfig` authorization rules
3. Modify role assignment in `CustomAuthProvider`
4. Update template role checks

## Environment Configuration

### Application Properties
- **Database**: PostgreSQL connection in `application.properties`
- **File Upload**: Configure `file.upload-path` for resume storage
- **Context Path**: Application runs at `/ats` context
- **Debug**: Enabled for development with full error details

### Default Credentials
- **Admin**: `admin@ats.com` / `Admin@ABC`
- **Test Candidates**: Multiple accounts with `Ats@ABC` password
- **New Users**: Default password from `user.default-password` property

## Troubleshooting

### Lombok Issues
- Enable annotation processing in IDE
- Install Lombok plugin
- Use IDE-based execution instead of Maven build

### Database Connection
- Verify PostgreSQL is running
- Check credentials in `application.properties`
- Ensure `spring-ats` database exists

### File Upload Errors
- Verify upload directory exists with write permissions
- Check file size limits in configuration
- Ensure proper file path in `application.properties`

## Testing Strategy

### Unit Testing
- Minimal JUnit tests (only context loading test exists)
- Focus on service layer testing for AI algorithms

### Functional Testing
- Use provided shell scripts for end-to-end testing
- Test AI screening with sample resumes
- Verify role-based access patterns

### Manual Testing
- Test complete recruitment workflow
- Verify AI scoring accuracy
- Check file upload and download functionality

This system is particularly valuable for organizations needing automated candidate screening with explainable AI results, making it ideal for high-volume recruitment scenarios.