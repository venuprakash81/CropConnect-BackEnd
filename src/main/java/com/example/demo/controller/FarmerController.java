
package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.entity.Farmer;
import com.example.demo.service.FarmerService;

import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin(origins = "http://localhost:3000")
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
            if (farmer.getAadhaarNumber() != null &&
                    !farmer.getAadhaarNumber().isBlank() &&
                    !farmer.getAadhaarNumber().matches("\\d{12}")) {

                return ResponseEntity.badRequest().body(
                        Map.of("message",
                                "Aadhaar number must contain 12 digits")
                );
            }

            if (farmer.getPhone() == null ||
                    !farmer.getPhone().matches("\\d{10}")) {

                return ResponseEntity.badRequest().body(
                        Map.of("message",
                                "Phone number must contain 10 digits")
                );
            }

            if (farmer.getLatitude() == null ||
                    farmer.getLongitude() == null) {

                return ResponseEntity.badRequest().body(
                        Map.of("message", "Farm location is required")
                );
            }

            if (farmer.getLatitude() < -90 ||
                    farmer.getLatitude() > 90 ||
                    farmer.getLongitude() < -180 ||
                    farmer.getLongitude() > 180) {

                return ResponseEntity.badRequest().body(
                        Map.of("message",
                                "Invalid latitude or longitude")
                );
            }

            Files.createDirectories(uploadDirectory);

            if (profilePhoto != null && !profilePhoto.isEmpty()) {

                if (profilePhoto.getContentType() == null ||
                        !profilePhoto.getContentType()
                                .startsWith("image/")) {

                    return ResponseEntity.badRequest().body(
                            Map.of("message",
                                    "Please upload a valid image")
                    );
                }

                String originalName =
                        profilePhoto.getOriginalFilename();

                String extension = "";

                if (originalName != null &&
                        originalName.lastIndexOf(".") >= 0) {

                    extension = originalName.substring(
                            originalName.lastIndexOf(".")
                    );
                }

                String fileName = UUID.randomUUID() + extension;
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

                farmer.setProfilePhoto(fileName);
            }

            Farmer savedFarmer =
                    farmerService.registerFarmer(farmer);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Farmer registered successfully");
            response.put("farmerId", savedFarmer.getId());
            response.put("farmer", savedFarmer);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            e.getMessage() == null
                                    ? "Farmer registration failed"
                                    : e.getMessage())
            );

        } catch (IOException e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(
                    Map.of("message", "Unable to save profile photo")
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(
                    Map.of("message", "Farmer registration failed")
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
                            .map(farmer -> {
                                Map<String, Object> item =
                                        new HashMap<>();

                                item.put("id", farmer.getId());
                                item.put("fullName",
                                        farmer.getFullName());
                                item.put("email", farmer.getEmail());
                                item.put("phone", farmer.getPhone());
                                item.put("alternativePhone",
                                        farmer.getAlternativePhone());
                                item.put("profilePhoto",
                                        farmer.getProfilePhoto());
                                item.put("aadhaarNumber",
                                        farmer.getAadhaarNumber());
                                item.put("dateOfBirth",
                                        farmer.getDateOfBirth());
                                item.put("village", farmer.getVillage());
                                item.put("mandal", farmer.getMandal());
                                item.put("district",
                                        farmer.getDistrict());
                                item.put("state", farmer.getState());
                                item.put("pincode", farmer.getPincode());
                                item.put("latitude",
                                        farmer.getLatitude());
                                item.put("longitude",
                                        farmer.getLongitude());
                                item.put("totalLand",
                                        farmer.getTotalLand());
                                item.put("landUnit",
                                        farmer.getLandUnit());

                                return item;
                            })
                            .toList();

            return ResponseEntity.ok(farmers);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(
                    Map.of("message", "Unable to retrieve farmers")
            );
        }
    }

    // =========================================
    // GET FARMER BY ID
    // GET /api/farmers/{id}
    // =========================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getFarmer(@PathVariable Long id) {
        try {
            Farmer farmer = farmerService.getFarmerById(id);

            Map<String, Object> response = new HashMap<>();

            response.put("id", farmer.getId());
            response.put("fullName", farmer.getFullName());
            response.put("email", farmer.getEmail());
            response.put("phone", farmer.getPhone());
            response.put("alternativePhone",
                    farmer.getAlternativePhone());
            response.put("profilePhoto", farmer.getProfilePhoto());
            response.put("aadhaarNumber", farmer.getAadhaarNumber());
            response.put("dateOfBirth", farmer.getDateOfBirth());
            response.put("village", farmer.getVillage());
            response.put("mandal", farmer.getMandal());
            response.put("district", farmer.getDistrict());
            response.put("state", farmer.getState());
            response.put("pincode", farmer.getPincode());
            response.put("latitude", farmer.getLatitude());
            response.put("longitude", farmer.getLongitude());
            response.put("totalLand", farmer.getTotalLand());
            response.put("landUnit", farmer.getLandUnit());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
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
            @RequestParam String email) {

        try {
            Farmer farmer = farmerService.getFarmerByEmail(
                    email.trim()
            );

            Map<String, Object> response = new HashMap<>();

            response.put("id", farmer.getId());
            response.put("fullName", farmer.getFullName());
            response.put("email", farmer.getEmail());
            response.put("phone", farmer.getPhone());
            response.put("alternativePhone",
                    farmer.getAlternativePhone());
            response.put("profilePhoto", farmer.getProfilePhoto());
            response.put("aadhaarNumber", farmer.getAadhaarNumber());
            response.put("dateOfBirth", farmer.getDateOfBirth());
            response.put("village", farmer.getVillage());
            response.put("mandal", farmer.getMandal());
            response.put("district", farmer.getDistrict());
            response.put("state", farmer.getState());
            response.put("pincode", farmer.getPincode());
            response.put("latitude", farmer.getLatitude());
            response.put("longitude", farmer.getLongitude());
            response.put("totalLand", farmer.getTotalLand());
            response.put("landUnit", farmer.getLandUnit());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
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
                        Map.of("message",
                                "Phone number must contain 10 digits")
                );
            }

            if (updatedFarmer.getAadhaarNumber() != null &&
                    !updatedFarmer.getAadhaarNumber().isBlank() &&
                    !updatedFarmer.getAadhaarNumber()
                            .matches("\\d{12}")) {

                return ResponseEntity.badRequest().body(
                        Map.of("message",
                                "Aadhaar number must contain 12 digits")
                );
            }

            if (updatedFarmer.getLatitude() == null ||
                    updatedFarmer.getLongitude() == null ||
                    updatedFarmer.getLatitude() < -90 ||
                    updatedFarmer.getLatitude() > 90 ||
                    updatedFarmer.getLongitude() < -180 ||
                    updatedFarmer.getLongitude() > 180) {

                return ResponseEntity.badRequest().body(
                        Map.of("message",
                                "Valid farm coordinates are required")
                );
            }

            Files.createDirectories(uploadDirectory);

            if (profilePhoto != null && !profilePhoto.isEmpty()) {

                if (profilePhoto.getContentType() == null ||
                        !profilePhoto.getContentType()
                                .startsWith("image/")) {

                    return ResponseEntity.badRequest().body(
                            Map.of("message",
                                    "Please upload a valid image")
                    );
                }

                String originalName =
                        profilePhoto.getOriginalFilename();

                String extension = "";

                if (originalName != null &&
                        originalName.lastIndexOf(".") >= 0) {

                    extension = originalName.substring(
                            originalName.lastIndexOf(".")
                    );
                }

                String fileName = UUID.randomUUID() + extension;
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

            Map<String, Object> response = new HashMap<>();
            response.put("message",
                    "Farmer profile updated successfully");
            response.put("farmer", savedFarmer);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            e.getMessage() == null
                                    ? "Unable to update farmer profile"
                                    : e.getMessage())
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
    public ResponseEntity<?> deleteFarmer(@PathVariable Long id) {
        try {
            farmerService.deleteFarmer(id);

            return ResponseEntity.ok(
                    Map.of("message", "Farmer deleted successfully")
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("message",
                            e.getMessage() == null
                                    ? "Farmer not found"
                                    : e.getMessage())
            );

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(
                    Map.of("message", "Unable to delete farmer")
            );
        }
    }

    // =========================================
    // GET FARMER PROFILE PHOTO
    // GET /api/farmers/profile-photo/{fileName}
    // =========================================
    @GetMapping("/profile-photo/{fileName:.+}")
    public ResponseEntity<byte[]> getProfilePhoto(
            @PathVariable String fileName) {

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

            String contentType = Files.probeContentType(filePath);

            if (contentType == null ||
                    !contentType.startsWith("image/")) {
                contentType =
                        MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(image);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // =========================================
    // CHANGE FARMER PASSWORD
    // POST /api/farmers/change-password
    // =========================================
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody ChangePasswordRequest request) {

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

            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "message",
                            "Unable to change password"
                    )
            );
        }
    }
}
