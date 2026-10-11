
package com.example.demo.controller;

import com.example.demo.entity.Buyer;
import com.example.demo.service.BuyerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/buyers")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:5173"
})
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @PostMapping(
        value = "/register",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> registerBuyer(
            @RequestPart("buyer") Buyer buyer,
            @RequestPart(value = "profilePhoto", required = false)
            MultipartFile profilePhoto) {
        try {
            Buyer saved = buyerService.register(buyer, profilePhoto);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of("message", "Registration successful.",
                       "buyer", saved)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message", e.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Map.of("message", "Buyer registration failed.")
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginBuyer(
            @RequestBody Map<String, String> data) {
        try {
            if (data == null || data.get("email") == null
                    || data.get("password") == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("message", "Email and password are required.")
                );
            }

            Buyer buyer = buyerService.login(
                data.get("email").trim(),
                data.get("password")
            );

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Login successful.");
            response.put("buyerId", buyer.getId());
            response.put("email", buyer.getEmail());
            response.put("buyer", buyer);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("message", e.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Map.of("message", "Buyer login failed.")
            );
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getBuyerByEmail(
            @PathVariable String email) {
        try {
            return ResponseEntity.ok(buyerService.getBuyerByEmail(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("message", e.getMessage())
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBuyerById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(buyerService.getBuyerById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("message", e.getMessage())
            );
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBuyers() {
        List<Buyer> buyers = buyerService.getAllBuyers();
        return ResponseEntity.ok(buyers);
    }

    // Update profile details by login email.
    @PutMapping("/profile/{email}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String email,
            @RequestBody Buyer updatedBuyer) {
        try {
            Buyer saved = buyerService.updateBuyerProfile(
                email, updatedBuyer
            );
            return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully.",
                "buyer", saved
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message", e.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Map.of("message", "Unable to update profile.")
            );
        }
    }

    // Change password after checking the current password.
    @PutMapping("/change-password/{email}")
    public ResponseEntity<?> changePassword(
            @PathVariable String email,
            @RequestBody Map<String, String> data) {
        try {
            if (data == null
                    || data.get("currentPassword") == null
                    || data.get("newPassword") == null
                    || data.get("confirmPassword") == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("message", "Complete all password fields.")
                );
            }

            buyerService.changeBuyerPassword(
                email,
                data.get("currentPassword"),
                data.get("newPassword"),
                data.get("confirmPassword")
            );

            return ResponseEntity.ok(
                Map.of("message", "Password changed successfully.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message", e.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Map.of("message", "Unable to change password.")
            );
        }
    }
}
