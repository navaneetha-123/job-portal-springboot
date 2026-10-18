package com.jobportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // public pages
                .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/favicon.ico").permitAll()
                // auth endpoints
                .requestMatchers(HttpMethod.POST, "/register", "/login", "/api/logout").permitAll()
                // anyone can view jobs
                .requestMatchers(HttpMethod.GET, "/jobs").permitAll()
                // only employers manage jobs
                .requestMatchers(HttpMethod.POST, "/jobs").hasRole("EMPLOYER")
                .requestMatchers(HttpMethod.PUT, "/jobs/**").hasRole("EMPLOYER")
                .requestMatchers(HttpMethod.DELETE, "/jobs/**").hasRole("EMPLOYER")
                // only job seekers apply
                .requestMatchers("/apply", "/applications/**").hasRole("JOB_SEEKER")
                .anyRequest().authenticated())
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }
}