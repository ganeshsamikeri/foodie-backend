package com.foodie.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try {
            // Test database connection
            try (Connection conn = dataSource.getConnection()) {
                if (conn.isValid(2)) { // 2 second timeout
                    return ResponseEntity.ok("✅ Application is healthy! Database connection successful.");
                }
            }
            return ResponseEntity.status(500).body("❌ Database connection test failed.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Health check failed: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("🚀 Foodie Backend API is running!");
    }

    @GetMapping("/env-check")
    public ResponseEntity<String> envCheck() {
        StringBuilder sb = new StringBuilder();
        sb.append("Environment Variables Check:\n");
        sb.append("DB_URL: ").append(System.getenv("DB_URL")).append("\n");
        sb.append("DB_USERNAME: ").append(System.getenv("DB_USERNAME")).append("\n");
        sb.append("PORT: ").append(System.getenv("PORT")).append("\n");
        sb.append("All Env Vars: ").append(System.getenv());

        return ResponseEntity.ok(sb.toString());
    }
}