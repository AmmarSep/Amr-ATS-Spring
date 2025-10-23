#!/bin/bash

echo "=========================================="
echo "ATS Implementation Verification"
echo "=========================================="
echo ""

# Check database tables
echo "1. Checking Database Tables..."
psql -U ammar.s.s -d spring-ats -c "SELECT COUNT(*) as job_postings_count FROM job_postings;" -t 2>/dev/null
if [ $? -eq 0 ]; then
    echo "   ✓ job_postings table exists"
else
    echo "   ✗ job_postings table missing"
fi

psql -U ammar.s.s -d spring-ats -c "SELECT COUNT(*) as applications_count FROM applications;" -t 2>/dev/null
if [ $? -eq 0 ]; then
    echo "   ✓ applications table exists"
else
    echo "   ✗ applications table missing"
fi
echo ""

# Check Thymeleaf views
echo "2. Checking Thymeleaf Views..."
VIEWS=(
    "src/main/resources/templates/recruitment/job-list.html"
    "src/main/resources/templates/recruitment/job-detail.html"
    "src/main/resources/templates/recruitment/apply.html"
    "src/main/resources/templates/recruitment/applications.html"
)

for view in "${VIEWS[@]}"; do
    if [ -f "$view" ]; then
        echo "   ✓ $(basename $view)"
    else
        echo "   ✗ $(basename $view) missing"
    fi
done
echo ""

# Check Java files
echo "3. Checking Java Implementation..."
JAVA_FILES=(
    "src/main/java/com/spring/getready/config/SecurityConfig.java"
    "src/main/java/com/spring/getready/controller/RecruitmentController.java"
    "src/main/java/com/spring/getready/services/RecruitmentService.java"
    "src/main/java/com/spring/getready/services/AIResumeScreeningService.java"
    "src/main/java/com/spring/getready/services/UploadFileService.java"
)

for file in "${JAVA_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✓ $(basename $file)"
    else
        echo "   ✗ $(basename $file) missing"
    fi
done
echo ""

# Check test resumes
echo "4. Checking Test Resumes..."
TEST_FILES=(
    "test-resumes/sample-resume-high-match.txt"
    "test-resumes/sample-resume-medium-match.txt"
    "test-resumes/sample-resume-low-match.txt"
    "test-resumes/test-ai-screening.sh"
)

for file in "${TEST_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✓ $(basename $file)"
    else
        echo "   ✗ $(basename $file) missing"
    fi
done
echo ""

# Check SecurityConfig for role permissions
echo "5. Checking SecurityConfig Role Permissions..."
if grep -q "RECRUITER" src/main/java/com/spring/getready/config/SecurityConfig.java; then
    echo "   ✓ RECRUITER role configured"
else
    echo "   ✗ RECRUITER role not found"
fi

if grep -q "CANDIDATE" src/main/java/com/spring/getready/config/SecurityConfig.java; then
    echo "   ✓ CANDIDATE role configured"
else
    echo "   ✗ CANDIDATE role not found"
fi
echo ""

# Summary
echo "=========================================="
echo "Verification Complete"
echo "=========================================="
echo ""
echo "Next Steps:"
echo "1. Run: mvn clean install"
echo "2. Run: mvn spring-boot:run"
echo "3. Access: http://localhost:8080/ats"
echo "4. Test recruitment workflow with sample resumes"
echo ""
