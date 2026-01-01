package com.foodie.backend.config;

import com.foodie.backend.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ❌ Disable CSRF (JWT + REST)
                .csrf(csrf -> csrf.disable())

                // ✅ ENABLE CORS (IMPORTANT)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ❌ Disable sessions
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔐 Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // 🌐 PUBLIC ENDPOINTS
                        .requestMatchers(
                                "/",
                                "/api/health",
                                "/api/auth/**"
                        ).permitAll()

                        // 👤 USER + ADMIN
                        .requestMatchers("/api/orders/**")
                        .hasAnyRole("USER", "ADMIN")

                        // 🔐 ADMIN ONLY
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // 🔒 EVERYTHING ELSE
                        .anyRequest().authenticated()
                )

                // 🔑 JWT FILTER
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * ✅ CORS CONFIGURATION
     * Required for browser + frontend + Render
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://your-frontend.vercel.app" // change if needed
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
