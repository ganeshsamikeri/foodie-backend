package com.foodie.backend.controller;

import com.foodie.backend.dto.LoginRequest;
import com.foodie.backend.model.User;
import com.foodie.backend.repository.UserRepository;
import com.foodie.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // =========================
    // ✅ REGISTER API
    // =========================
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {

        // check duplicate email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // default role
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        userRepository.save(user);

        return Map.of(
                "message", "User registered successfully",
                "email", user.getEmail(),
                "role", user.getRole()
        );
    }

    // =========================
    // ✅ LOGIN API
    // =========================
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // password check
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 🔥 generate JWT with ROLE
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return Map.of(
                "token", token,
                "email", user.getEmail(),
                "role", user.getRole()
        );
    }
}
