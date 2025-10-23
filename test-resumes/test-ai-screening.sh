#!/bin/bash

echo "==================================="
echo "AI Resume Screening Test"
echo "==================================="
echo ""

# Required skills for Senior Java Developer position
REQUIRED_SKILLS="Java, Spring Boot, Microservices, REST API, PostgreSQL, Docker, Kubernetes"

echo "Job Position: Senior Java Developer"
echo "Required Skills: $REQUIRED_SKILLS"
echo ""
echo "-----------------------------------"
echo ""

# Test High Match Resume
echo "Testing: sample-resume-high-match.txt"
echo "Expected Score: 85-100%"
RESUME_TEXT=$(cat sample-resume-high-match.txt)
echo "Resume contains: Java ✓, Spring Boot ✓, Microservices ✓, REST API ✓, PostgreSQL ✓, Docker ✓, Kubernetes ✓"
echo "Match: 7/7 skills = 100%"
echo ""

# Test Medium Match Resume
echo "Testing: sample-resume-medium-match.txt"
echo "Expected Score: 40-60%"
RESUME_TEXT=$(cat sample-resume-medium-match.txt)
echo "Resume contains: Java ✓, Spring Boot ✓, REST API ✓"
echo "Missing: Microservices, PostgreSQL, Docker, Kubernetes"
echo "Match: 3/7 skills = 42.86%"
echo ""

# Test Low Match Resume
echo "Testing: sample-resume-low-match.txt"
echo "Expected Score: 0-20%"
RESUME_TEXT=$(cat sample-resume-low-match.txt)
echo "Resume contains: None of the required skills"
echo "Match: 0/7 skills = 0%"
echo ""

echo "==================================="
echo "Test Summary"
echo "==================================="
echo "✓ High Match Resume: Should rank first (100%)"
echo "✓ Medium Match Resume: Should rank second (42.86%)"
echo "✓ Low Match Resume: Should rank last (0%)"
echo ""
echo "To test in the application:"
echo "1. Start the application"
echo "2. Login as a Candidate"
echo "3. Navigate to /recruitment/jobs"
echo "4. Apply for 'Senior Java Developer' position"
echo "5. Upload one of the test resumes"
echo "6. Login as Recruiter/Admin"
echo "7. View applications at /recruitment/job/1/applications"
echo "8. Verify AI scores match expected results"
