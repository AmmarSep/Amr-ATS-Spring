package com.spring.getready.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        System.out.println("=== Configuring DataSource ===");
        
        // Try different environment variable names that Railway might use
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            databaseUrl = System.getenv("POSTGRES_URL");
        }
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            databaseUrl = System.getenv("POSTGRESQL_URL");
        }
        
        // Print all environment variables for debugging
        System.out.println("=== Environment Variables Debug ===");
        System.getenv().entrySet().stream()
            .filter(entry -> entry.getKey().contains("DATABASE") || 
                           entry.getKey().contains("POSTGRES") || 
                           entry.getKey().contains("PG"))
            .forEach(entry -> {
                String value = entry.getValue();
                if (value != null && value.contains("postgresql://")) {
                    value = value.replaceAll("://([^:]+):([^@]+)@", "://***:***@");
                }
                System.out.println(entry.getKey() + "=" + value);
            });
        System.out.println("=== End Debug ===");
        
        HikariConfig config = new HikariConfig();
        
        try {
            if (databaseUrl != null && !databaseUrl.isEmpty() && !databaseUrl.startsWith("${")) {
                System.out.println("Using DATABASE_URL: " + databaseUrl.replaceAll("://([^:]+):([^@]+)@", "://***:***@"));
                
                // Parse Railway DATABASE_URL format: postgresql://user:password@host:port/database
                URI dbUri = new URI(databaseUrl);
                
                String userInfo = dbUri.getUserInfo();
                if (userInfo == null || !userInfo.contains(":")) {
                    throw new RuntimeException("Invalid DATABASE_URL format: missing or invalid user info");
                }
                
                String[] userInfoParts = userInfo.split(":");
                String username = userInfoParts[0];
                String password = userInfoParts.length > 1 ? userInfoParts[1] : "";
                String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
                
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                
                System.out.println("Parsed JDBC URL: " + jdbcUrl);
                System.out.println("Username: " + username);
                System.out.println("Host: " + dbUri.getHost());
                System.out.println("Port: " + dbUri.getPort());
                System.out.println("Database: " + dbUri.getPath());
                
            } else {
                System.err.println("ERROR: No DATABASE_URL found in environment!");
                System.err.println("Available environment variables:");
                System.getenv().keySet().stream().sorted().forEach(System.err::println);
                throw new RuntimeException("DATABASE_URL environment variable is required for production deployment");
            }
        } catch (Exception e) {
            System.err.println("Error configuring database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to configure database connection", e);
        }
        
        // Configure connection pool with more lenient settings
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(60000); // 60 seconds
        config.setIdleTimeout(300000); // 5 minutes
        config.setMaxLifetime(900000); // 15 minutes
        config.setLeakDetectionThreshold(60000);
        
        System.out.println("=== DataSource Configuration Complete ===");
        return new HikariDataSource(config);
    }
}