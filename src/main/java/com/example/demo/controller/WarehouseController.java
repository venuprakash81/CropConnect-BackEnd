
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
@CrossOrigin(
    origins = {
        "https://cropconnect-venuprakash.vercel.app"
    }
)
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
        String savedPhotoPath = null;
        String savedDocumentPath = null;

        try {
            if (warehouse.getWarehouseName() == null
                    || warehouse.getWarehouseName().isBlank()
                    || warehouse.getOwnerName() == null
                    || warehouse.getOwnerName().isBlank()
                    || warehouse.getEmail() == null
                    || warehouse.getEmail().isBlank()
                    || warehouse.getPassword() == null
                    || warehouse.getPassword().isBlank()) {
                return response(
                    HttpStatus.BAD_REQUEST,
                    "Warehouse name, owner name, email and password are required."
                );
            }

            if (warehouse.getPhone() == null
                    || warehouse.getPhone().isBlank()) {
                return response(
                    HttpStatus.BAD_REQUEST,
                    "Phone number is required."
                );
            }

            if (warehouse.getTotalStorageCapacity() <= 0) {
                return response(
                    HttpStatus.BAD_REQUEST,
                    "Storage capacity must be greater than zero."
                );
            }

            warehouse.setEmail(
                warehouse.getEmail().trim().toLowerCase()
            );

            Files.createDirectories(uploadDirectory);

            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                validateUpload(profilePhoto, true);
                savedPhotoPath = saveFile(profilePhoto);
                warehouse.setProfilePhoto(savedPhotoPath);
            }

            if (warehouseDocument != null
                    && !warehouseDocument.isEmpty()) {
                validateUpload(warehouseDocument, false);
                savedDocumentPath = saveFile(warehouseDocument);
                warehouse.setWarehouseDocument(savedDocumentPath);
            }

            Warehouse saved =
                warehouseService.registerWarehouse(warehouse);

            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("message", "Warehouse registered successfully.");
            body.put("warehouseId", saved.getId());
            body.put("email", saved.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(body);

        } catch (DataIntegrityViolationException ex) {
            deleteIfExists(savedPhotoPath);
            deleteIfExists(savedDocumentPath);

            return response(
                HttpStatus.CONFLICT,
                "This email or phone number is already registered."
            );

        } catch (IllegalArgumentException ex) {
            deleteIfExists(savedPhotoPath);
            deleteIfExists(savedDocumentPath);

            return response(HttpStatus.BAD_REQUEST, ex.getMessage());

        } catch (IOException ex) {
            deleteIfExists(savedPhotoPath);
            deleteIfExists(savedDocumentPath);

            return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to save the uploaded files."
            );

        } catch (Exception ex) {
            deleteIfExists(savedPhotoPath);
            deleteIfExists(savedDocumentPath);

            return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Warehouse registration failed. Check the backend logs."
            );
        }
    }

    private void validateUpload(
        MultipartFile file,
        boolean imageOnly
    ) {
        String original = file.getOriginalFilename();

        if (original == null || original.isBlank()) {
            throw new IllegalArgumentException(
                "Invalid uploaded filename."
            );
        }

        String lower = original.toLowerCase();

        boolean allowed = imageOnly
            ? lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".png")
                || lower.endsWith(".webp")
            : lower.endsWith(".pdf")
                || lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".png");

        if (!allowed) {
            throw new IllegalArgumentException(
                imageOnly
                    ? "Profile photo must be JPG, JPEG, PNG or WEBP."
                    : "Document must be PDF, JPG, JPEG or PNG."
            );
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException(
                "Each uploaded file must be 5 MB or smaller."
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

    private void deleteIfExists(String filename) {
        if (filename == null) {
            return;
        }

        try {
            Files.deleteIfExists(uploadDirectory.resolve(filename).normalize());
        } catch (IOException ignored) {
            // Cleanup failure must not hide the original error.
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestBody Map<String, String> request
    ) {
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null) {
            return response(
                HttpStatus.BAD_REQUEST,
                "Email and password are required."
            );
        }

        Optional<Warehouse> result =
            warehouseService.login(email.trim().toLowerCase(), password);

        if (result.isEmpty()) {
            return response(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password."
            );
        }

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", "Login successful.");
        body.put("warehouse", result.get());

        return ResponseEntity.ok(body);
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
            return response(HttpStatus.NOT_FOUND, "Warehouse not found.");
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
            return response(HttpStatus.NOT_FOUND, "Warehouse not found.");
        }

        return ResponseEntity.ok(warehouse.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWarehouse(
        @PathVariable Long id,
        @RequestBody Warehouse warehouse
    ) {
        try {
            Warehouse updated =
                warehouseService.updateWarehouse(id, warehouse);

            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException ex) {
            return response(HttpStatus.BAD_REQUEST, ex.getMessage());
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
            return response(
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

            return response(
                HttpStatus.OK,
                "Password changed successfully."
            );

        } catch (IllegalArgumentException ex) {
            return response(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable Long id) {
        try {
            warehouseService.deleteWarehouse(id);

            return response(
                HttpStatus.OK,
                "Warehouse deleted successfully."
            );

        } catch (IllegalArgumentException ex) {
            return response(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> response(
        HttpStatus status,
        String message
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", status.is2xxSuccessful());
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}
