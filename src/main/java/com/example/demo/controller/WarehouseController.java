
package com.example.demo.controller;

import com.example.demo.entity.Warehouse;
import com.example.demo.service.WarehouseService;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouses")
@CrossOrigin(origins = {
    "https://cropconnect-venuprakash.vercel.app"
})
public class WarehouseController {

    private final WarehouseService warehouseService;

    private final Path uploadDirectory =
        Paths.get("uploads", "warehouses").toAbsolutePath().normalize();

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping(
        value = "/register",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> registerWarehouse(
        @RequestPart("warehouse") Warehouse warehouse,
        @RequestPart(value = "profilePhoto", required = false)
        MultipartFile profilePhoto,
        @RequestPart(value = "warehouseDocument", required = false)
        MultipartFile warehouseDocument
    ) {
        String savedPhoto = null;
        String savedDocument = null;

        try {
            if (warehouse.getWarehouseName() == null
                    || warehouse.getWarehouseName().isBlank()
                    || warehouse.getOwnerName() == null
                    || warehouse.getOwnerName().isBlank()
                    || warehouse.getEmail() == null
                    || warehouse.getEmail().isBlank()
                    || warehouse.getPassword() == null
                    || warehouse.getPassword().isBlank()) {
                return message(
                    HttpStatus.BAD_REQUEST,
                    "Warehouse name, owner name, email and password are required."
                );
            }

            if (warehouse.getPhone() == null
                    || !warehouse.getPhone().matches("\\d{10}")) {
                return message(
                    HttpStatus.BAD_REQUEST,
                    "Enter a valid 10-digit phone number."
                );
            }

            if (warehouse.getTotalStorageCapacity() == null
                    || warehouse.getTotalStorageCapacity() <= 0) {
                return message(
                    HttpStatus.BAD_REQUEST,
                    "Storage capacity must be greater than zero."
                );
            }

            if (warehouse.getLatitude() == null
                    || warehouse.getLongitude() == null
                    || warehouse.getLatitude() < -90
                    || warehouse.getLatitude() > 90
                    || warehouse.getLongitude() < -180
                    || warehouse.getLongitude() > 180) {
                return message(
                    HttpStatus.BAD_REQUEST,
                    "A valid latitude and longitude are required."
                );
            }

            if (warehouse.getPassword().length() < 6) {
                return message(
                    HttpStatus.BAD_REQUEST,
                    "Password must contain at least 6 characters."
                );
            }

            warehouse.setEmail(
                warehouse.getEmail().trim().toLowerCase()
            );

            Files.createDirectories(uploadDirectory);

            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                validateFile(profilePhoto, true);
                savedPhoto = saveFile(profilePhoto);
                warehouse.setProfilePhoto(savedPhoto);
            }

            if (warehouseDocument != null
                    && !warehouseDocument.isEmpty()) {
                validateFile(warehouseDocument, false);
                savedDocument = saveFile(warehouseDocument);
                warehouse.setWarehouseDocument(savedDocument);
            }

            Warehouse saved =
                warehouseService.registerWarehouse(warehouse);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Warehouse registered successfully.");
            result.put("warehouseId", saved.getId());
            result.put("email", saved.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (DataIntegrityViolationException ex) {
            deleteFile(savedPhoto);
            deleteFile(savedDocument);

            return message(
                HttpStatus.CONFLICT,
                "This email is already registered."
            );

        } catch (IllegalArgumentException ex) {
            deleteFile(savedPhoto);
            deleteFile(savedDocument);

            return message(HttpStatus.BAD_REQUEST, ex.getMessage());

        } catch (IOException ex) {
            deleteFile(savedPhoto);
            deleteFile(savedDocument);

            return message(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to save uploaded files."
            );

        } catch (Exception ex) {
            deleteFile(savedPhoto);
            deleteFile(savedDocument);

            return message(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Registration failed. Check the backend application logs."
            );
        }
    }

    private void validateFile(MultipartFile file, boolean imageOnly) {
        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Invalid file.");
        }

        String name = filename.toLowerCase();

        boolean valid = imageOnly
            ? name.endsWith(".jpg")
                || name.endsWith(".jpeg")
                || name.endsWith(".png")
                || name.endsWith(".webp")
            : name.endsWith(".pdf")
                || name.endsWith(".jpg")
                || name.endsWith(".jpeg")
                || name.endsWith(".png");

        if (!valid) {
            throw new IllegalArgumentException(
                imageOnly
                    ? "Profile photo must be JPG, JPEG, PNG or WEBP."
                    : "Document must be PDF, JPG, JPEG or PNG."
            );
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException(
                "Each file must be 5 MB or smaller."
            );
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        String extension = "";

        if (original != null && original.lastIndexOf('.') >= 0) {
            extension = original.substring(original.lastIndexOf('.'))
                .toLowerCase()
                .replaceAll("[^a-z0-9.]", "");
        }

        String filename = UUID.randomUUID() + extension;
        Path destination = uploadDirectory.resolve(filename).normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new IOException("Invalid file path.");
        }

        file.transferTo(destination.toFile());
        return filename;
    }

    private void deleteFile(String filename) {
        if (filename == null) {
            return;
        }

        try {
            Files.deleteIfExists(
                uploadDirectory.resolve(filename).normalize()
            );
        } catch (IOException ignored) {
            // Do not hide the original registration error.
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestBody Map<String, String> request
    ) {
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null
                || email.isBlank() || password.isBlank()) {
            return message(
                HttpStatus.BAD_REQUEST,
                "Email and password are required."
            );
        }

        Optional<Warehouse> warehouse =
            warehouseService.login(email.trim().toLowerCase(), password);

        if (warehouse.isEmpty()) {
            return message(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password."
            );
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Login successful.");
        result.put("warehouse", warehouse.get());

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<?> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWarehouseById(@PathVariable Long id) {
        Optional<Warehouse> warehouse =
            warehouseService.getWarehouseById(id);

        if (warehouse.isEmpty()) {
            return message(HttpStatus.NOT_FOUND, "Warehouse not found.");
        }

        return ResponseEntity.ok(warehouse.get());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getWarehouseByEmail(
        @PathVariable String email
    ) {
        Optional<Warehouse> warehouse =
            warehouseService.getWarehouseByEmail(email);

        if (warehouse.isEmpty()) {
            return message(HttpStatus.NOT_FOUND, "Warehouse not found.");
        }

        return ResponseEntity.ok(warehouse.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWarehouse(
        @PathVariable Long id,
        @RequestBody Warehouse warehouse
    ) {
        try {
            return ResponseEntity.ok(
                warehouseService.updateWarehouse(id, warehouse)
            );
        } catch (IllegalArgumentException ex) {
            return message(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
        @PathVariable Long id,
        @RequestBody Map<String, String> request
    ) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || newPassword == null
                || newPassword.length() < 6) {
            return message(
                HttpStatus.BAD_REQUEST,
                "Provide the current password and a new password of at least 6 characters."
            );
        }

        try {
            warehouseService.changePassword(
                id,
                currentPassword,
                newPassword
            );

            return message(HttpStatus.OK, "Password changed successfully.");
        } catch (IllegalArgumentException ex) {
            return message(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable Long id) {
        try {
            warehouseService.deleteWarehouse(id);

            return message(HttpStatus.OK, "Warehouse deleted successfully.");
        } catch (IllegalArgumentException ex) {
            return message(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> message(
        HttpStatus status,
        String text
    ) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", status.is2xxSuccessful());
        result.put("message", text);

        return ResponseEntity.status(status).body(result);
    }
}
