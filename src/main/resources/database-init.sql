-- Database initialization script for Railway deployment
-- This script will create the necessary tables and initial data

-- Create tables (if they don't exist)
CREATE TABLE IF NOT EXISTS user_group (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL,
    short_group VARCHAR(10) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS user_details (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_locked BOOLEAN DEFAULT FALSE,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_on TIMESTAMP,
    user_uuid VARCHAR(255),
    group_ref INTEGER REFERENCES user_group(group_id),
    family_ref INTEGER
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

-- Insert user groups
INSERT INTO user_group (group_name, short_group, is_active) 
SELECT 'Administrator', 'ADM', TRUE 
WHERE NOT EXISTS (SELECT 1 FROM user_group WHERE short_group = 'ADM');

INSERT INTO user_group (group_name, short_group, is_active) 
SELECT 'Recruiter', 'REC', TRUE 
WHERE NOT EXISTS (SELECT 1 FROM user_group WHERE short_group = 'REC');

INSERT INTO user_group (group_name, short_group, is_active) 
SELECT 'Candidate', 'CAN', TRUE 
WHERE NOT EXISTS (SELECT 1 FROM user_group WHERE short_group = 'CAN');

INSERT INTO user_group (group_name, short_group, is_active) 
SELECT 'Student', 'STU', TRUE 
WHERE NOT EXISTS (SELECT 1 FROM user_group WHERE short_group = 'STU');

-- Insert default admin user (password: Ats@ABC)
INSERT INTO user_details (username, email, password, group_ref, is_locked) 
VALUES ('Admin User', 'admin@spring.ats', '$2a$10$N.kmMOB8gCp9OA.pGqWqge5IZ/Ww8i0V8ShjB0m90FRm2oj9.rH.S', 
    (SELECT group_id FROM user_group WHERE short_group = 'ADM'), FALSE)
ON CONFLICT (email) DO NOTHING;

-- Insert sample recruiter (password: Ats@ABC)
INSERT INTO user_details (username, email, password, group_ref, is_locked) 
VALUES ('Sample Recruiter', 'recruiter@spring.ats', '$2a$10$N.kmMOB8gCp9OA.pGqWqge5IZ/Ww8i0V8ShjB0m90FRm2oj9.rH.S', 
    (SELECT group_id FROM user_group WHERE short_group = 'REC'), FALSE)
ON CONFLICT (email) DO NOTHING;