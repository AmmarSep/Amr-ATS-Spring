package com.spring.getready.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("=== Starting Database Initialization ===");
        
        try {
            // Wait a moment for Hibernate to create tables
            Thread.sleep(2000);
            
            // Check if admin user already exists
            Integer userCount = 0;
            try {
                userCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_details WHERE email = 'admin@spring.ats'", 
                    Integer.class
                );
            } catch (Exception e) {
                System.out.println("User_details table not found or error checking: " + e.getMessage());
                System.out.println("Attempting to create missing tables and default users...");
                initializeDatabase();
                return;
            }
            
            if (userCount == 0) {
                System.out.println("Admin user not found, creating default users...");
                createDefaultUsers();
            } else {
                System.out.println("Admin user already exists, skipping initialization");
            }
            
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== Database Initialization Complete ===");
    }
    
    private void initializeDatabase() throws Exception {
        System.out.println("Running full database initialization script...");
        
        // Read the database-init.sql file
        ClassPathResource resource = new ClassPathResource("database-init.sql");
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            String sqlScript = reader.lines().collect(Collectors.joining("\n"));
            
            // Split by semicolon and execute each statement
            String[] statements = sqlScript.split(";");
            
            for (String statement : statements) {
                String trimmedStatement = statement.trim();
                if (!trimmedStatement.isEmpty() && !trimmedStatement.startsWith("--")) {
                    try {
                        jdbcTemplate.execute(trimmedStatement);
                        System.out.println("Executed: " + trimmedStatement.substring(0, Math.min(50, trimmedStatement.length())) + "...");
                    } catch (Exception e) {
                        System.err.println("Error executing statement: " + trimmedStatement.substring(0, Math.min(50, trimmedStatement.length())));
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("Database initialization script completed successfully");
            
        } catch (Exception e) {
            System.err.println("Error reading database-init.sql: " + e.getMessage());
            throw e;
        }
    }
    
    private void createDefaultUsers() throws Exception {
        System.out.println("Creating default users...");
        
        // Insert default admin user (password: Ats@ABC)
        try {
            jdbcTemplate.update(
                "INSERT INTO user_details (username, email, password, group_ref, is_locked) " +
                "VALUES (?, ?, ?, (SELECT group_id FROM user_group WHERE short_group = ?), ?) ON CONFLICT (email) DO NOTHING",
                "Admin User", 
                "admin@spring.ats", 
                "$2a$10$N.kmMOB8gCp9OA.pGqWqge5IZ/Ww8i0V8ShjB0m90FRm2oj9.rH.S", 
                "ADM", 
                false
            );
            System.out.println("Created admin user: admin@spring.ats");
        } catch (Exception e) {
            System.err.println("Error creating admin user: " + e.getMessage());
        }
        
        // Insert sample recruiter (password: Ats@ABC)
        try {
            jdbcTemplate.update(
                "INSERT INTO user_details (username, email, password, group_ref, is_locked) " +
                "VALUES (?, ?, ?, (SELECT group_id FROM user_group WHERE short_group = ?), ?) ON CONFLICT (email) DO NOTHING",
                "Sample Recruiter", 
                "recruiter@spring.ats", 
                "$2a$10$N.kmMOB8gCp9OA.pGqWqge5IZ/Ww8i0V8ShjB0m90FRm2oj9.rH.S", 
                "REC", 
                false
            );
            System.out.println("Created sample recruiter: recruiter@spring.ats");
        } catch (Exception e) {
            System.err.println("Error creating recruiter user: " + e.getMessage());
        }
        
        // Insert sample candidate (password: Ats@ABC)
        try {
            jdbcTemplate.update(
                "INSERT INTO user_details (username, email, password, group_ref, is_locked) " +
                "VALUES (?, ?, ?, (SELECT group_id FROM user_group WHERE short_group = ?), ?) ON CONFLICT (email) DO NOTHING",
                "Test Candidate", 
                "candidate@ats.com", 
                "$2a$10$N.kmMOB8gCp9OA.pGqWqge5IZ/Ww8i0V8ShjB0m90FRm2oj9.rH.S", 
                "CAN", 
                false
            );
            System.out.println("Created sample candidate: candidate@ats.com");
        } catch (Exception e) {
            System.err.println("Error creating candidate user: " + e.getMessage());
        }
        
        System.out.println("Default users creation completed");
    }
}