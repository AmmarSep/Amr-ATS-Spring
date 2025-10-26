package com.spring.getready.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired(required = false)
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("application", "Spring ATS");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("profile", System.getProperty("spring.profiles.active", "default"));
        
        System.out.println("Health check requested at: " + LocalDateTime.now());
        
        if (dataSource == null) {
            status.put("status", "PARTIAL");
            status.put("database", "NO_DATASOURCE");
            status.put("message", "DataSource not configured");
            System.out.println("Health check: DataSource is null");
            return ResponseEntity.ok(status); // Return 200 even without DB for initial startup
        }
        
        try {
            // Test database connection with timeout
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(10)) {
                    status.put("database", "UP");
                    status.put("status", "UP");
                    System.out.println("Health check: Database connection is healthy");
                } else {
                    status.put("database", "DOWN");
                    status.put("status", "DEGRADED");
                    System.out.println("Health check: Database connection validation failed");
                }
            }
            return ResponseEntity.ok(status);
        } catch (SQLException e) {
            status.put("status", "DEGRADED");
            status.put("database", "DOWN");
            status.put("error", e.getMessage());
            System.err.println("Health check: Database error - " + e.getMessage());
            return ResponseEntity.ok(status); // Return 200 to keep container running
        } catch (Exception e) {
            status.put("status", "ERROR");
            status.put("database", "UNKNOWN");
            status.put("error", e.getMessage());
            System.err.println("Health check: Unexpected error - " + e.getMessage());
            return ResponseEntity.status(503).body(status);
        }
    }
}