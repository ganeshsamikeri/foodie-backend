package com.foodie.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequestMapping("/api") // ✅ IMPORTANT
public class HealthController {

    @Autowired
    private DataSource dataSource;

    // ✅ PUBLIC HEALTH CHECK
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try (Connection conn = dataSource.getConnection()) {

            if (conn.isValid(2)) {
                return ResponseEntity.ok("✅ OK - App & DB are healthy");
            }
            return ResponseEntity.status(500).body("❌ DB not responding");

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Health check failed: " + e.getMessage());
        }
    }

    // ✅ ROOT
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("🚀 Foodie Backend API is running");
    }

    // ⚠️ OPTIONAL (DEV ONLY)
    @GetMapping("/env-check")
    public ResponseEntity<String> envCheck() {
        return ResponseEntity.ok(
                "PORT=" + System.getenv("PORT") + "\n" +
                        "DB_URL=" + System.getenv("DB_URL")
        );
    }
}
