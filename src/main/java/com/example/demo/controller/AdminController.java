package com.example.demo.controller;

import com.example.demo.entity.Admin;
import com.example.demo.repository.AdminRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null ||
                email.trim().isEmpty() || password.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Email and password are required"));
        }

        Admin admin = adminRepository
                .findByEmail(email.trim())
                .orElse(null);

        if (admin == null ||
                !admin.getPassword().equals(password)) {

            return ResponseEntity.status(401)
                    .body(Map.of("message",
                            "Invalid email or password"));
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful",
                        "email", admin.getEmail()
                )
        );
    }
}