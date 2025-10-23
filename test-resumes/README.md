# AI Resume Screening Test Suite

This directory contains sample resumes for testing the AI-based resume screening functionality.

## Test Resumes

### 1. sample-resume-high-match.txt
- **Candidate:** John Doe - Senior Java Developer
- **Experience:** 5+ years
- **Expected AI Score:** 100%
- **Matched Skills:** All 7 required skills (Java, Spring Boot, Microservices, REST API, PostgreSQL, Docker, Kubernetes)
- **Use Case:** Tests perfect skill match scenario

### 2. sample-resume-medium-match.txt
- **Candidate:** Jane Smith - Software Developer
- **Experience:** 3 years
- **Expected AI Score:** 42.86%
- **Matched Skills:** 3 out of 7 (Java, Spring Boot, REST API)
- **Use Case:** Tests partial skill match scenario

### 3. sample-resume-low-match.txt
- **Candidate:** Alex Johnson - Frontend Developer
- **Experience:** 2 years
- **Expected AI Score:** 0%
- **Matched Skills:** None
- **Use Case:** Tests no skill match scenario

## Testing Instructions

### Manual Testing via Web Interface

1. **Start the Application**
   ```bash
   cd /Users/ammar.s.s/Code/spring-getready-placements
   mvn spring-boot:run
   ```

2. **Access the Application**
   - URL: http://localhost:8080/ats
   - Login as Candidate (create account if needed)

3. **Apply for Job**
   - Navigate to: `/recruitment/jobs`
   - Click on "Senior Java Developer" position
   - Click "Apply Now"
   - Upload one of the test resumes
   - Submit application

4. **View AI Screening Results**
   - Logout and login as Recruiter/Admin
   - Navigate to: `/recruitment/job/1/applications`
   - Verify AI scores and matched keywords

### Expected Results

| Resume | AI Score | Matched Keywords | Ranking |
|--------|----------|------------------|---------|
| High Match | 100% | java, spring boot, microservices, rest api, postgresql, docker, kubernetes | 1st |
| Medium Match | 42.86% | java, spring boot, rest api | 2nd |
| Low Match | 0% | (none) | 3rd |

## AI Screening Algorithm

The AI screening service uses keyword matching:
- Extracts required skills from job posting
- Searches for each skill in resume text (case-insensitive)
- Calculates match percentage: (matched_skills / total_skills) × 100
- Stores matched keywords for recruiter review
- Ranks candidates by AI score (descending)

## Notes

- Resume text extraction currently supports plain text files
- For production, integrate PDF/DOC parsing libraries (Apache PDFBox, Apache POI)
- AI algorithm can be enhanced with NLP, semantic matching, and experience weighting
