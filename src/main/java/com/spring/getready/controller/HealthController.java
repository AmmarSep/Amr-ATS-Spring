package com.spring.getready.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        
        try {
            // Test database connection
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    status.put("database", "UP");
                } else {
                    status.put("database", "DOWN");
                }
            }
            status.put("status", "UP");
            status.put("application", "Spring ATS");
            return ResponseEntity.ok(status);
        } catch (SQLException e) {
            status.put("status", "DOWN");
            status.put("database", "DOWN");
            status.put("error", e.getMessage());
            return ResponseEntity.status(503).body(status);
        }
    }
}