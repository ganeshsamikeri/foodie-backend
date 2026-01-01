package com.foodie.backend.config;

import com.foodie.backend.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // 🌐 PUBLIC
                        .requestMatchers(
                                "/",
                                "/api/test",
                                "/api/auth/**"
                        ).permitAll()

                        // 🔐 ADMIN ONLY
                        .requestMatchers("/api/orders/admin/**")
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
