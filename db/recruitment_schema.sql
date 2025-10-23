-- AI-Enabled ATS - Recruitment Schema Migration
-- Run this after restoring the base spring-ats.sql

-- Job Postings Table
CREATE TABLE IF NOT EXISTS job_postings (
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

-- Applications Table with AI Scoring
CREATE TABLE IF NOT EXISTS applications (
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

-- Update User Groups for Recruitment
INSERT INTO user_groups (group_name, group_description) 
VALUES ('Recruiter', 'HR/Recruiter role for managing recruitment process')
ON CONFLICT (group_name) DO NOTHING;

-- Update Student group to Candidate
UPDATE user_groups 
SET group_name = 'Candidate', 
    group_description = 'Job Candidate applying for positions'
WHERE group_name = 'Student';

-- Create indexes for performance
CREATE INDEX idx_job_active ON job_postings(is_active);
CREATE INDEX idx_job_posted_on ON job_postings(posted_on DESC);
CREATE INDEX idx_app_job ON applications(job_ref);
CREATE INDEX idx_app_candidate ON applications(candidate_ref);
CREATE INDEX idx_app_score ON applications(ai_score DESC);
CREATE INDEX idx_app_status ON applications(status);

-- Sample Job Posting (Optional)
INSERT INTO job_postings (job_title, job_description, required_skills, experience_required, location, job_type, is_active)
VALUES (
    'Senior Java Developer',
    'We are looking for an experienced Java developer to join our team. The ideal candidate will have strong experience in Spring Boot, microservices, and cloud technologies.',
    'Java, Spring Boot, Microservices, REST API, PostgreSQL, Docker, Kubernetes',
    '3-5 years',
    'Remote',
    'Full-time',
    TRUE
);

COMMENT ON TABLE job_postings IS 'Stores job posting information created by recruiters';
COMMENT ON TABLE applications IS 'Stores candidate applications with AI-based screening scores';
COMMENT ON COLUMN applications.ai_score IS 'AI-calculated match score (0-100) based on resume analysis';
COMMENT ON COLUMN applications.ai_match_keywords IS 'Comma-separated matched skills from resume';
