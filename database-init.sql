-- Database initialization script for Railway deployment
-- This script will create the necessary tables and initial data

-- Create tables (if they don't exist)
CREATE TABLE IF NOT EXISTS user_details (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_locked BOOLEAN DEFAULT FALSE,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_group_short_group VARCHAR(10) DEFAULT 'STU'
);

CREATE TABLE IF NOT EXISTS job_postings (
    job_id SERIAL PRIMARY KEY,
    job_title VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    job_type VARCHAR(100),
    experience_required VARCHAR(255),
    job_description TEXT,
    required_skills TEXT,
    posted_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    user_detail_id INTEGER REFERENCES user_details(user_id)
);

CREATE TABLE IF NOT EXISTS upload_files (
    file_id SERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_original_name VARCHAR(255),
    file_size BIGINT,
    content_type VARCHAR(255),
    uploaded_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS applications (
    application_id SERIAL PRIMARY KEY,
    job_ref INTEGER REFERENCES job_postings(job_id),
    candidate_ref INTEGER REFERENCES user_details(user_id),
    resume_ref INTEGER REFERENCES upload_files(file_id),
    applied_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Submitted',
    ai_score NUMERIC(5,2),
    ai_match_keywords TEXT,
    interview_scheduled_on TIMESTAMP,
    interview_date DATE,
    interview_time TIME,
    interviewer_name VARCHAR(255),
    interview_location VARCHAR(255),
    notes TEXT
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_app_candidate ON applications(candidate_ref);
CREATE INDEX IF NOT EXISTS idx_app_job ON applications(job_ref);
CREATE INDEX IF NOT EXISTS idx_app_score ON applications(ai_score DESC);
CREATE INDEX IF NOT EXISTS idx_app_status ON applications(status);

-- Insert default admin user (password: Ats@ABC)
INSERT INTO user_details (username, email, password, user_group_short_group, is_locked) 
VALUES ('Admin User', 'admin@spring.ats', '$2a$10$N.kmMOB8gCp9OA.pGqWqge5IZ/Ww8i0V8ShjB0m90FRm2oj9.rH.S', 'ADM', FALSE)
ON CONFLICT (email) DO NOTHING;

-- Insert sample recruiter (password: Ats@ABC)
INSERT INTO user_details (username, email, password, user_group_short_group, is_locked) 
VALUES ('Sample Recruiter', 'recruiter@spring.ats', '$2a$10$N.kmMOB8gCp9OA.pGqWqge5IZ/Ww8i0V8ShjB0m90FRm2oj9.rH.S', 'REC', FALSE)
ON CONFLICT (email) DO NOTHING;