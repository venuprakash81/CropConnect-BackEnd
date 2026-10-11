package com.example.demo.controller;

import com.example.demo.entity.Crop;
import com.example.demo.service.CropService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/crops")
@CrossOrigin(origins = {
        "https://cropconnect-venuprakash.vercel.app"
})
public class CropController {

    @Autowired
    private CropService cropService;

    private final Path uploadDirectory =
            Paths.get("uploads/crops").toAbsolutePath().normalize();

    @PostMapping("/add")
    public ResponseEntity<?> addCrop(
            @RequestParam("farmerId") Long farmerId,
            @RequestParam("cropName") String cropName,
            @RequestParam(value = "cropType", required = false) String cropType,
            @RequestParam(value = "quantity", required = false) Double quantity,
            @RequestParam(value = "unit", required = false) String unit,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "harvestDate", required = false) String harvestDate,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            if (farmerId == null || cropName == null || cropName.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Farmer ID and crop name are required"));
            }

            if (quantity != null && quantity < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Quantity cannot be negative"));
            }

            if (price != null && price <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Price must be greater than zero"));
            }

            Crop crop = new Crop();
            crop.setFarmerId(farmerId);
            crop.setCropName(cropName.trim());
            crop.setCropType(cropType);
            crop.setQuantity(quantity);
            crop.setUnit(unit);
            crop.setPrice(price);
            crop.setLocation(location);
            crop.setDescription(description);

            if (harvestDate != null && !harvestDate.isBlank()) {
                crop.setHarvestDate(LocalDate.parse(harvestDate));
            }

            if (image != null && !image.isEmpty()) {
                crop.setImageUrl("/uploads/crops/" + saveImage(image));
            }

            return ResponseEntity.ok(cropService.saveCrop(crop));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Unable to add crop: "
                            + safeMessage(e)));
        }
    }

    @GetMapping
    public ResponseEntity<List<Crop>> getAllCrops() {
        return ResponseEntity.ok(cropService.getAllCrops());
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<Crop>> getCropsByFarmer(
            @PathVariable Long farmerId) {
        return ResponseEntity.ok(cropService.getCropsByFarmer(farmerId));
    }

    @GetMapping("/farmer/email/{email}")
    public ResponseEntity<?> getCropsByFarmerEmail(
            @PathVariable String email) {
        try {
            return ResponseEntity.ok(
                    cropService.getCropsByFarmerEmail(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCropById(@PathVariable Integer id) {
        return cropService.getCropById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCrop(
            @PathVariable Integer id,
            @RequestParam("farmerId") Long farmerId,
            @RequestParam("cropName") String cropName,
            @RequestParam(value = "cropType", required = false) String cropType,
            @RequestParam(value = "quantity", required = false) Double quantity,
            @RequestParam(value = "unit", required = false) String unit,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "harvestDate", required = false) String harvestDate,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            Crop crop = cropService.getCropById(id).orElse(null);

            if (crop == null) {
                return ResponseEntity.notFound().build();
            }

            if (crop.getFarmerId() == null
                    || !crop.getFarmerId().equals(farmerId)) {
                return ResponseEntity.status(403)
                        .body(Map.of("message", "Not authorized to update this crop"));
            }

            if (cropName == null || cropName.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Crop name is required"));
            }

            if (quantity != null && quantity < 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Quantity cannot be negative"));
            }

            if (price != null && price <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Price must be greater than zero"));
            }

            crop.setCropName(cropName.trim());
            crop.setCropType(cropType);
            crop.setQuantity(quantity);
            crop.setUnit(unit);
            crop.setPrice(price);
            crop.setLocation(location);
            crop.setDescription(description);
            crop.setHarvestDate(
                    harvestDate == null || harvestDate.isBlank()
                            ? null : LocalDate.parse(harvestDate));

            if (image != null && !image.isEmpty()) {
                crop.setImageUrl("/uploads/crops/" + saveImage(image));
            }

            return ResponseEntity.ok(cropService.saveCrop(crop));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Unable to update crop: "
                            + safeMessage(e)));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCrop(
            @PathVariable Integer id,
            @RequestParam(required = false) Long farmerId) {

        Crop crop = cropService.getCropById(id).orElse(null);

        if (crop == null) {
            return ResponseEntity.notFound().build();
        }

        if (farmerId != null && !farmerId.equals(crop.getFarmerId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Not authorized to delete this crop"));
        }

        try {
            cropService.deleteCrop(id);
            return ResponseEntity.ok(
                    Map.of("message", "Crop deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Cannot delete crop while bookings reference it"));
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (image.getSize() > 5 * 1024 * 1024) {
            throw new IOException("Image must be 5 MB or smaller");
        }

        String contentType = image.getContentType();
        if (contentType == null ||
                !List.of("image/jpeg", "image/png", "image/webp")
                        .contains(contentType)) {
            throw new IOException("Only JPEG, PNG and WebP images are allowed");
        }

        Files.createDirectories(uploadDirectory);

        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IOException("Unsupported image type");
        };

        String fileName = UUID.randomUUID() + extension;
        Path filePath = uploadDirectory.resolve(fileName).normalize();

        if (!filePath.startsWith(uploadDirectory)) {
            throw new IOException("Invalid filename");
        }

        Files.copy(image.getInputStream(), filePath,
                StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private String safeMessage(Exception e) {
        return e.getMessage() == null ? "Unexpected error" : e.getMessage();
    }
    
}
