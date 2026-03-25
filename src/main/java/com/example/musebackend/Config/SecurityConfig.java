package com.example.musebackend.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final OAuth2SuccessHandler oauth2SuccessHandler;

    private static final String[] PUBLIC_URLS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/reset-password",
            "/api/auth/verify/reset-password",
            "/api/auth/verify",
            "/api/auth/resend-otp",
            "/api/health",
            "/login/**",
            "/oauth2/**",
            "/api/auth/oauth2/success"
    };

    private static final String[] USER_ADMIN_ACCESS = {
            "/api/songs/**",
            "/api/albums/**",
            "/api/playlists/**",
            "/api/music/download/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(HttpMethod.GET, USER_ADMIN_ACCESS).hasAnyRole("USER", "ADMIN")
                        .anyRequest().hasRole("ADMIN")
                )

                // --- OAuth2 Configuration ---
                .oauth2Login(oauth -> oauth
                        .successHandler(oauth2SuccessHandler)
                )

                // --- Session Management ---
                // We use IF_REQUIRED because OAuth2 needs a temporary session for the redirect flow.
                // Your JwtFilter will still handle subsequent requests as stateless.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // --- Filter Chain ---
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}