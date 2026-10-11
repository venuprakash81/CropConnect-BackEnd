
package com.example.demo.controller;

import com.example.demo.dto.TransporterResponse;
import com.example.demo.entity.Transporter;
import com.example.demo.service.TransporterService;

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

@RestController
@RequestMapping("/api/transporters")
@CrossOrigin(origins = {
        "https://cropconnect-venuprakash.vercel.app"
})
public class TransporterController {

    private final TransporterService service;

    private final Path uploadDirectory =
            Paths.get("uploads/transporter-documents").toAbsolutePath()
                    .normalize();

    public TransporterController(TransporterService service) {
        this.service = service;
    }

    private String saveFile(MultipartFile file, String prefix)
            throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Files.createDirectories(uploadDirectory);

        String originalName = Paths.get(
                file.getOriginalFilename() == null
                        ? "document"
                        : file.getOriginalFilename()
        ).getFileName().toString();

        String fileName = System.currentTimeMillis()
                + "_" + prefix + "_" + originalName;

        Path destination = uploadDirectory.resolve(fileName).normalize();

        if (!destination.startsWith(uploadDirectory)) {
            throw new IOException("Invalid file path");
        }

        Files.copy(file.getInputStream(), destination,
                StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private ResponseEntity<?> error(
            int status, String message) {
        return ResponseEntity.status(status)
                .body(Map.of("message", message));
    }

    // REGISTER
    @PostMapping(value = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerTransporter(
            @RequestPart("transporter") Transporter transporter,
            @RequestPart(value = "profilePhoto", required = false)
                    MultipartFile profilePhoto,
            @RequestPart(value = "drivingLicence", required = false)
                    MultipartFile drivingLicence,
            @RequestPart(value = "vehicleRC", required = false)
                    MultipartFile vehicleRC,
            @RequestPart(value = "insurance", required = false)
                    MultipartFile insurance,
            @RequestPart(value = "fitnessCertificate", required = false)
                    MultipartFile fitnessCertificate,
            @RequestPart(value = "pollutionCertificate", required = false)
                    MultipartFile pollutionCertificate) {

        try {
            if (transporter.getPhone() == null
                    || !transporter.getPhone().matches("\\d{10}")) {
                return error(400, "Phone number must contain 10 digits");
            }

            if (transporter.getEmail() == null
                    || transporter.getEmail().isBlank()
                    || transporter.getPassword() == null
                    || transporter.getPassword().isBlank()) {
                return error(400, "Email and password are required");
            }

            if (transporter.getAadhaarNumber() != null
                    && !transporter.getAadhaarNumber().isBlank()
                    && !transporter.getAadhaarNumber().matches("\\d{12}")) {
                return error(400, "Aadhaar number must contain 12 digits");
            }

            if (transporter.getLatitude() == null
                    || transporter.getLongitude() == null
                    || transporter.getLatitude() < -90
                    || transporter.getLatitude() > 90
                    || transporter.getLongitude() < -180
                    || transporter.getLongitude() > 180) {
                return error(400, "Valid transporter coordinates are required");
            }

            transporter.setProfilePhoto(saveFile(profilePhoto, "profile"));
            transporter.setDrivingLicenceFile(saveFile(drivingLicence, "licence"));
            transporter.setVehicleRCFile(saveFile(vehicleRC, "rc"));
            transporter.setInsuranceFile(saveFile(insurance, "insurance"));
            transporter.setFitnessCertificateFile(
                    saveFile(fitnessCertificate, "fitness"));
            transporter.setPollutionCertificateFile(
                    saveFile(pollutionCertificate, "pollution"));

            Transporter saved = service.registerTransporter(transporter);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Transporter registered successfully");
            response.put("transporterId", saved.getId());
            response.put("transporter",
                    service.getProfileResponse(saved.getId()));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        } catch (IOException e) {
            return error(500, "Unable to save uploaded documents");
        } catch (Exception e) {
            return error(500, "Transporter registration failed");
        }
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> loginTransporter(
            @RequestBody Map<String, String> loginData) {

        String email = loginData.get("email");
        String password = loginData.get("password");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            return error(400, "Email and password are required");
        }

        try {
            Transporter t = service.loginTransporter(email, password);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("transporterId", t.getId());
            response.put("transporter",
                    service.getProfileResponse(t.getId()));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return error(401, "Invalid email or password");
        }
    }

    // ALL TRANSPORTERS
    @GetMapping("/all")
    public ResponseEntity<List<TransporterResponse>> getAllTransporters() {
        List<TransporterResponse> result = service.getAllTransporters()
                .stream()
                .map(t -> new TransporterResponse(
                        t.getId(),
                        t.getFullName(),
                        t.getPhone(),
                        t.getEmail(),
                        t.getProfilePhoto(),
                        t.getBusinessName(),
                        t.getTransporterType(),
                        t.getVillage(),
                        t.getDistrict(),
                        t.getState(),
                        t.getVehicleNumber(),
                        t.getVehicleType(),
                        t.getVehicleCapacity(),
                        t.getCapacityUnit(),
                        t.getLatitude(),
                        t.getLongitude(),
                        t.getCurrentlyAvailable()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    // VIEW PROFILE
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getTransporterProfile(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getProfileResponse(id));
        } catch (IllegalArgumentException e) {
            return error(404, "Transporter not found");
        }
    }

    // EDIT PROFILE
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateTransporterProfile(
            @PathVariable Long id,
            @RequestBody Transporter input) {
        try {
            return ResponseEntity.ok(
                    service.updateTransporterProfile(id, input));
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        }
    }

    // CHANGE PASSWORD
    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> data) {

        String currentPassword = data.get("currentPassword");
        String newPassword = data.get("newPassword");
        String confirmPassword = data.get("confirmPassword");

        if (newPassword == null
                || !newPassword.equals(confirmPassword)) {
            return error(400, "New password and confirmation must match");
        }

        try {
            service.changeTransporterPassword(
                    id, currentPassword, newPassword);

            return ResponseEntity.ok(
                    Map.of("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        }
    }

    // GET TRANSPORTER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransporter(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getProfileResponse(id));
        } catch (IllegalArgumentException e) {
            return error(404, "Transporter not found");
        }
    }

    // GET PROFILE PHOTO
    @GetMapping("/profile-photo/{fileName:.+}")
    public ResponseEntity<byte[]> getProfilePhoto(
            @PathVariable String fileName) {
        try {
            Path path = uploadDirectory.resolve(fileName).normalize();

            if (!path.startsWith(uploadDirectory)
                    || !Files.isRegularFile(path)) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(path);
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(Files.readAllBytes(path));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET LOCATION
    @GetMapping("/{id}/location")
    public ResponseEntity<?> getLocation(@PathVariable Long id) {
        try {
            Transporter t = service.getTransporterById(id);

            if (t.getLatitude() == null || t.getLongitude() == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(Map.of(
                    "transporterId", t.getId(),
                    "name", t.getFullName(),
                    "latitude", t.getLatitude(),
                    "longitude", t.getLongitude()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE LOCATION
    @PutMapping("/{id}/location")
    public ResponseEntity<?> updateLocation(
            @PathVariable Long id,
            @RequestBody Map<String, Double> data) {
        try {
            Transporter t = service.updateLocation(
                    id, data.get("latitude"), data.get("longitude"));

            return ResponseEntity.ok(Map.of(
                    "message", "Location updated",
                    "latitude", t.getLatitude(),
                    "longitude", t.getLongitude()
            ));
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        }
    }

    // DELETE TRANSPORTER
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransporter(@PathVariable Long id) {
        try {
            service.deleteTransporter(id);
            return ResponseEntity.ok(
                    Map.of("message", "Transporter deleted successfully"));
        } catch (IllegalArgumentException e) {
            return error(404, "Transporter not found");
        }
    }
}
