package com.foodie.backend.config;

import com.foodie.backend.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ❌ Disable CSRF (required for REST APIs & Postman)
                .csrf(csrf -> csrf.disable())

                // ✅ Enable CORS (frontend + Postman)
                .cors(cors -> {})

                // 🔐 Stateless JWT security
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔑 Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // 🌐 PUBLIC APIs (NO TOKEN REQUIRED)
                        .requestMatchers(
                                "/",
                                "/api/health",
                                "/api/test",
                                "/api/auth/**"
                        ).permitAll()

                        // 🔴 ADMIN ONLY APIs
                        .requestMatchers("/api/orders/admin/**")
                        .hasRole("ADMIN")

                        // 👤 USER + ADMIN APIs
                        .requestMatchers("/api/orders/**")
                        .authenticated()

                        // 🔒 Everything else needs authentication
                        .anyRequest().authenticated()
                )

                // 🔄 JWT filter
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // ✅ Required for authentication (login)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // ✅ Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
