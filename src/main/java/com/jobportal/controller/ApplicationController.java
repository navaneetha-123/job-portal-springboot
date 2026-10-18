package com.jobportal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.model.Application;
import com.jobportal.model.User;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.UserRepository;

@RestController
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationController(ApplicationRepository applicationRepository,
                                 UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody Application request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
        }

        if (applicationRepository.existsByUserIdAndJobId(user.getId(), request.getJobId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Already applied"));
        }

        Application app = new Application();
        app.setUserId(user.getId());
        app.setJobId(request.getJobId());
        return ResponseEntity.ok(applicationRepository.save(app));
    }

    @GetMapping("/applications/me")
    public ResponseEntity<?> myApplications(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
        }
        List<Application> list = applicationRepository.findByUserId(user.getId());
        return ResponseEntity.ok(list);
    }
}