package com.foodie.backend.config;

import com.foodie.backend.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // ✅ Explicit constructor (fixes IntelliJ + Lombok confusion)
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // 🌍 PUBLIC
                        .requestMatchers(
                                "/",
                                "/api/health",
                                "/api/auth/**"
                        ).permitAll()

                        // 🔐 ADMIN ONLY
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // 👤 USER & ADMIN
                        .requestMatchers("/api/orders/**")
                        .authenticated()

                        // 🔒 EVERYTHING ELSE
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
