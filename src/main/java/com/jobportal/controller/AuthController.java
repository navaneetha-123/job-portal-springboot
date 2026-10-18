package com.jobportal.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        if (blank(user.getName()) || blank(user.getEmail()) || blank(user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already registered"));
        }
        // only two roles allowed from signup; anything else becomes JOB_SEEKER
        String role = "EMPLOYER".equals(user.getRole()) ? "EMPLOYER" : "JOB_SEEKER";
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User login,
            HttpServletRequest request, HttpServletResponse response) {
        if (blank(login.getEmail()) || blank(login.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password required"));
        }
        Optional<User> found = userRepository.findByEmail(login.getEmail());
        if (found.isEmpty() || !passwordEncoder.matches(login.getPassword(), found.get().getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
        User u = found.get();
        String role = u.getRole() == null ? "JOB_SEEKER" : u.getRole();

        Authentication token = new UsernamePasswordAuthenticationToken(
                u.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
        new HttpSessionSecurityContextRepository().saveContext(context, request, response);

        return ResponseEntity.ok(Map.of("id", u.getId(), "name", u.getName(),
                "email", u.getEmail(), "role", role));
    }

    @PostMapping("/api/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}