package com.spring.getready;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class GetreadyApplication {

	public static void main(String[] args) {
		System.out.println("=== Starting Spring ATS Application ===");
		System.out.println("Java Version: " + System.getProperty("java.version"));
		System.out.println("Spring Profile: " + System.getProperty("spring.profiles.active"));
		System.out.println("Environment Variables:");
		System.getenv().entrySet().stream()
			.filter(entry -> entry.getKey().contains("DATABASE") || 
							entry.getKey().contains("POSTGRES") ||
							entry.getKey().contains("PORT") ||
							entry.getKey().contains("SPRING"))
			.forEach(entry -> System.out.println("  " + entry.getKey() + "=" + 
				(entry.getValue().contains("postgresql://") ? 
					entry.getValue().replaceAll("://([^:]+):([^@]+)@", "://***:***@") : 
					entry.getValue())));
		
		try {
			SpringApplication.run(GetreadyApplication.class, args);
			System.out.println("=== Application Started Successfully ===");
		} catch (Exception e) {
			System.err.println("=== Application Failed to Start ===");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
