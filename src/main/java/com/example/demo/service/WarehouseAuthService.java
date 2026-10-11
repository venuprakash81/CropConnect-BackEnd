package com.example.demo.service;

import com.example.demo.entity.Warehouse;
import com.example.demo.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

@Service
public class WarehouseAuthService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseAuthService(
            WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public Warehouse login(String email, String password) {
        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Email and password are required");
        }

        Warehouse warehouse = warehouseRepository.findByEmail(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"));

        // Temporary comparison for plain-text passwords.
        // Use BCrypt password hashing for a real application.
        if (warehouse.getPassword() == null
                || !warehouse.getPassword().equals(password)) {
            throw new IllegalArgumentException(
                    "Invalid email or password");
        }

        return warehouse;
    }
}