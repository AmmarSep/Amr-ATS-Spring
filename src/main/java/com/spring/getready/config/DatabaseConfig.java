package com.spring.getready.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        try {
            if (databaseUrl != null && !databaseUrl.isEmpty() && !databaseUrl.startsWith("${")) {
                System.out.println("Using DATABASE_URL: " + databaseUrl.replaceAll("://([^:]+):([^@]+)@", "://***:***@"));
                
                // Parse Railway DATABASE_URL format: postgresql://user:password@host:port/database
                URI dbUri = new URI(databaseUrl);
                
                String[] userInfo = dbUri.getUserInfo().split(":");
                String username = userInfo[0];
                String password = userInfo.length > 1 ? userInfo[1] : "";
                String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
                
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                
                System.out.println("Parsed JDBC URL: " + jdbcUrl);
                System.out.println("Username: " + username);
            } else {
                System.out.println("DATABASE_URL not found, using local configuration");
                // Fallback for local development
                config.setJdbcUrl("jdbc:postgresql://localhost:5432/spring-ats");
                config.setUsername("ammar.s.s");
                config.setPassword("");
            }
        } catch (URISyntaxException e) {
            System.err.println("Error parsing DATABASE_URL: " + e.getMessage());
            throw new RuntimeException("Failed to parse DATABASE_URL", e);
        }
        
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        return new HikariDataSource(config);
    }
}