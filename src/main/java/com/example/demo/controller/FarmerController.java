
package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.entity.Farmer;
import com.example.demo.service.FarmerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/farmers")
@CrossOrigin(origins = {
    "https://cropconnect-venuprakash.vercel.app"
})
public class FarmerController {

    @Autowired
    private FarmerService farmerService;

    private final Path uploadDirectory =
            Paths.get("uploads/farmer-profiles")
                    .toAbsolutePath()
                    .normalize();

    // =========================================
    // REGISTER FARMER
    // POST /api/farmers/register
    // =========================================

    @PostMapping(
            value = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> registerFarmer(
            @RequestPart("farmer") Farmer farmer,
            @RequestPart(value = "profilePhoto", required = false)
            MultipartFile profilePhoto
    ) {
        try {
            // Validate email
            if (farmer.getEmail() == null ||
                    farmer.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Email is required")
                );
            }

            farmer.setEmail(
                    farmer.getEmail().trim().toLowerCase()
            );

            // Validate phone
            if (farmer.getPhone() == null ||
                    !farmer.getPhone().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Phone number must contain 10 digits"
                        )
                );
            }

            // Validate Aadhaar
            if (farmer.getAadhaarNumber() == null ||
                    !farmer.getAadhaarNumber().matches("\\d{12}")) {
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Aadhaar number must contain 12 digits"
                        )
                );
            }

            // Validate coordinates
            if (farmer.getLatitude() == null ||
                    farmer.getLongitude() == null ||
                    farmer.getLatitude() < -90 ||
                    farmer.getLatitude() > 90 ||
                    farmer.getLongitude() < -180 ||
                    farmer.getLongitude() > 180) {
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Please select a valid farm location"
                        )
                );
            }

            /*
             * Save the farmer first.
             * This ensures profile-photo filesystem errors do not
             * prevent an otherwise valid registration.
             */
            Farmer savedFarmer =
                    farmerService.registerFarmer(farmer);

            // =====================================
            // SAVE PROFILE PHOTO (OPTIONAL)
            // =====================================

            if (profilePhoto != null &&
                    !profilePhoto.isEmpty()) {

                try {
                    String contentType =
                            profilePhoto.getContentType();

                    if (contentType != null &&
                            contentType.startsWith("image/")) {

                        Files.createDirectories(uploadDirectory);

                        String originalName =
                                profilePhoto.getOriginalFilename();

                        String extension = "";

                        if (originalName != null &&
                                originalName.lastIndexOf(".") >= 0) {
                            extension = originalName.substring(
                                    originalName.lastIndexOf(".")
                            );
                        }

                        String fileName =
                                UUID.randomUUID() + extension;

                        Path filePath =
                                uploadDirectory.resolve(fileName)
                                        .normalize();

                        if (filePath.startsWith(uploadDirectory)) {

                            Files.copy(
                                    profilePhoto.getInputStream(),
                                    filePath,
                                    StandardCopyOption.REPLACE_EXISTING
                            );

                            savedFarmer.setProfilePhoto(fileName);

                            /*
                             * Persist the photo filename using the
                             * existing service update method.
                             */
                            savedFarmer = farmerService.updateFarmer(
                                    savedFarmer.getId(),
                                    savedFarmer
                            );
                        }
                    }
                } catch (Exception photoError) {
                    // Registration has already succeeded.
                    // Log the photo error without returning HTTP 500.
                    photoError.printStackTrace();
                }
            }

            // =====================================
            // SUCCESS RESPONSE
            // =====================================

            Map<String, Object> response = new HashMap<>();

            response.put("success", true);
            response.put(
                    "message",
                    "Farmer registered successfully"
            );
            response.put("farmerId", savedFarmer.getId());
            response.put("email", savedFarmer.getEmail());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (DataIntegrityViolationException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "This email or another unique field is already registered."
                            )
                    );

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    e.getMessage() == null
                                            ? "Unable to register farmer"
                                            : e.getMessage()
                            )
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Unable to complete farmer registration"
                            )
                    );
        }
    }

    // =========================================
    // GET ALL FARMERS
    // GET /api/farmers
    // =========================================

    @GetMapping
    public ResponseEntity<?> getAllFarmers() {
        try {
            List<Map<String, Object>> farmers =
                    farmerService.getAllFarmers()
                            .stream()
                            .map(this::toFarmerResponse)
                            .toList();

            return ResponseEntity.ok(farmers);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Unable to retrieve farmers"
                            )
                    );
        }
    }

    // =========================================
    // GET FARMER BY ID
    // GET /api/farmers/{id}
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getFarmer(
            @PathVariable Long id
    ) {
        try {
            Farmer farmer = farmerService.getFarmerById(id);

            return ResponseEntity.ok(
                    toFarmerResponse(farmer)
            );

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of("message", "Farmer not found")
                    );
        }
    }

    // =========================================
    // GET FARMER BY EMAIL
    // GET /api/farmers/by-email?email=...
    // =========================================

    @GetMapping("/by-email")
    public ResponseEntity<?> getFarmerByEmail(
            @RequestParam String email
    ) {
        try {
            Farmer farmer =
                    farmerService.getFarmerByEmail(
                            email.trim().toLowerCase()
                    );

            return ResponseEntity.ok(
                    toFarmerResponse(farmer)
            );

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of("message", "Farmer not found")
                    );
        }
    }

    // =========================================
    // UPDATE FARMER PROFILE
    // PUT /api/farmers/{id}
    // =========================================

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateFarmer(
            @PathVariable Long id,
            @RequestPart("farmer") Farmer updatedFarmer,
            @RequestPart(value = "profilePhoto", required = false)
            MultipartFile profilePhoto
    ) {
        try {
            if (updatedFarmer.getPhone() == null ||
                    !updatedFarmer.getPhone().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Phone number must contain 10 digits"
                        )
                );
            }

            if (updatedFarmer.getAadhaarNumber() != null &&
                    !updatedFarmer.getAadhaarNumber().isBlank() &&
                    !updatedFarmer.getAadhaarNumber()
                            .matches("\\d{12}")) {
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Aadhaar number must contain 12 digits"
                        )
                );
            }

            if (updatedFarmer.getLatitude() == null ||
                    updatedFarmer.getLongitude() == null ||
                    updatedFarmer.getLatitude() < -90 ||
                    updatedFarmer.getLatitude() > 90 ||
                    updatedFarmer.getLongitude() < -180 ||
                    updatedFarmer.getLongitude() > 180) {
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "message",
                                "Valid farm coordinates are required"
                        )
                );
            }

            if (profilePhoto != null &&
                    !profilePhoto.isEmpty()) {

                String contentType =
                        profilePhoto.getContentType();

                if (contentType == null ||
                        !contentType.startsWith("image/")) {
                    return ResponseEntity.badRequest().body(
                            Map.of(
                                    "message",
                                    "Please upload a valid image"
                            )
                    );
                }

                Files.createDirectories(uploadDirectory);

                String originalName =
                        profilePhoto.getOriginalFilename();

                String extension = "";

                if (originalName != null &&
                        originalName.lastIndexOf(".") >= 0) {
                    extension = originalName.substring(
                            originalName.lastIndexOf(".")
                    );
                }

                String fileName =
                        UUID.randomUUID() + extension;

                Path filePath =
                        uploadDirectory.resolve(fileName).normalize();

                if (!filePath.startsWith(uploadDirectory)) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Invalid file name")
                    );
                }

                Files.copy(
                        profilePhoto.getInputStream(),
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                );

                updatedFarmer.setProfilePhoto(fileName);
            }

            Farmer savedFarmer =
                    farmerService.updateFarmer(id, updatedFarmer);

            Map<String, Object> response =
                    toFarmerResponse(savedFarmer);

            response.put(
                    "message",
                    "Farmer profile updated successfully"
            );

            return ResponseEntity.ok(response);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                            "message",
                            "The email or another unique field is already in use"
                    )
            );

        } catch (RuntimeException e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            e.getMessage() == null
                                    ? "Unable to update farmer profile"
                                    : e.getMessage()
                    )
            );

        } catch (IOException e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(
                    Map.of("message", "Unable to save profile photo")
            );
        }
    }

    // =========================================
    // DELETE FARMER
    // DELETE /api/farmers/{id}
    // =========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFarmer(
            @PathVariable Long id
    ) {
        try {
            farmerService.deleteFarmer(id);

            return ResponseEntity.ok(
                    Map.of("message", "Farmer deleted successfully")
            );

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage() == null
                                            ? "Farmer not found"
                                            : e.getMessage()
                            )
                    );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Unable to delete farmer"
                            )
                    );
        }
    }

    // =========================================
    // GET FARMER PROFILE PHOTO
    // GET /api/farmers/profile-photo/{fileName}
    // =========================================

    @GetMapping("/profile-photo/{fileName:.+}")
    public ResponseEntity<byte[]> getProfilePhoto(
            @PathVariable String fileName
    ) {
        try {
            Path filePath =
                    uploadDirectory.resolve(fileName).normalize();

            if (!filePath.startsWith(uploadDirectory)) {
                return ResponseEntity.badRequest().build();
            }

            if (!Files.exists(filePath) ||
                    !Files.isRegularFile(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] image = Files.readAllBytes(filePath);

            String contentType =
                    Files.probeContentType(filePath);

            if (contentType == null ||
                    !contentType.startsWith("image/")) {
                contentType =
                        MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_TYPE,
                            contentType
                    )
                    .body(image);

        } catch (IOException e) {
            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    // =========================================
    // CHANGE FARMER PASSWORD
    // POST /api/farmers/change-password
    // =========================================

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody ChangePasswordRequest request
    ) {
        try {
            farmerService.changePassword(request);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Password changed successfully"
                    )
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            e.getMessage() == null
                                    ? "Invalid password request"
                                    : e.getMessage()
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Unable to change password"
                            )
                    );
        }
    }

    // =========================================
    // SAFE FARMER RESPONSE
    // Do not return passwords or Aadhaar numbers
    // =========================================

    private Map<String, Object> toFarmerResponse(
            Farmer farmer
    ) {
        Map<String, Object> item = new HashMap<>();

        item.put("id", farmer.getId());
        item.put("fullName", farmer.getFullName());
        item.put("email", farmer.getEmail());
        item.put("phone", farmer.getPhone());
        item.put(
                "alternativePhone",
                farmer.getAlternativePhone()
        );
        item.put("profilePhoto", farmer.getProfilePhoto());
        item.put("dateOfBirth", farmer.getDateOfBirth());
        item.put("village", farmer.getVillage());
        item.put("mandal", farmer.getMandal());
        item.put("district", farmer.getDistrict());
        item.put("state", farmer.getState());
        item.put("pincode", farmer.getPincode());
        item.put("latitude", farmer.getLatitude());
        item.put("longitude", farmer.getLongitude());
        item.put("totalLand", farmer.getTotalLand());
        item.put("landUnit", farmer.getLandUnit());

        return item;
    }
}
