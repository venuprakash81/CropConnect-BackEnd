package com.example.demo.controller;

import com.example.demo.entity.Warehouse;
import com.example.demo.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "http://localhost:5173"
})
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerWarehouse(
            @RequestBody Warehouse warehouse) {
        try {
            Warehouse saved =
                    warehouseService.registerWarehouse(warehouse);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(message(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(message("Email and password are required"));
        }

        Optional<Warehouse> result =
                warehouseService.login(email, password);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(message("Invalid email or password"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("warehouse", result.get());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllWarehouses() {
        return ResponseEntity.ok(
                warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(message("Warehouse not found")));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getWarehouseByEmail(
            @PathVariable String email) {
        return warehouseService.getWarehouseByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(message("Warehouse not found")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWarehouse(
            @PathVariable Long id,
            @RequestBody Warehouse warehouse) {
        try {
            return ResponseEntity.ok(
                    warehouseService.updateWarehouse(id, warehouse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(message(e.getMessage()));
        }
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                        .body(message(
                                "Current and new passwords are required"));
            }

            boolean changed = warehouseService.changePassword(
                    id, currentPassword, newPassword);

            if (!changed) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(message("Current password is incorrect"));
            }

            return ResponseEntity.ok(
                    message("Password changed successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(message(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable Long id) {
        try {
            warehouseService.deleteWarehouse(id);
            return ResponseEntity.ok(
                    message("Warehouse deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(message(e.getMessage()));
        }
    }

    private Map<String, String> message(String value) {
        Map<String, String> response = new HashMap<>();
        response.put("message", value);
        return response;
    }
}