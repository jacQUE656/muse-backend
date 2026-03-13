package com.example.musebackend.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private  JwtFilter jwtFilter;
    private static final String[] PUBLIC_URLS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/reset-password",
            "/api/auth/verify/reset-password",
            "/api/auth/verify",
            "/api/auth/resend-otp",
            "/api/health"
    };
    private static final String[] USER_ADMIN_ACCESS = {
            "/api/songs",
            "/api/albums",
            "/api/playlists",
            "/api/music/download"
    };




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return    http.cors(c->c.disable()).
                csrf(customizer-> customizer.disable()).
                authorizeHttpRequests(auth->
                        auth.requestMatchers(PUBLIC_URLS)
                                .permitAll()
                                .requestMatchers(HttpMethod.GET , USER_ADMIN_ACCESS)
                                .hasAnyRole("USER", "ADMIN")
                                .anyRequest()
                                .hasRole("ADMIN"))
                .sessionManagement(section -> section.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }





}
