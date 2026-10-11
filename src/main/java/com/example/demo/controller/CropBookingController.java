package com.example.demo.controller;

import com.example.demo.dto.CropBookingRequest;
import com.example.demo.entity.CropBooking;
import com.example.demo.service.CropBookingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crop-bookings")
@CrossOrigin(origins = {
        "https://cropconnect-venuprakash.vercel.app"
})
public class CropBookingController {

    private final CropBookingService bookingService;

    public CropBookingController(CropBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> bookCrop(
            @RequestBody CropBookingRequest request) {
        try {
            return ResponseEntity.ok(bookingService.bookCrop(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Booking failed"));
        }
    }

    @GetMapping
    public List<CropBooking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/buyer/{buyerId}")
    public List<CropBooking> getBuyerBookings(
            @PathVariable Long buyerId) {
        return bookingService.getBuyerBookings(buyerId);
    }

    @GetMapping("/buyer/email/{email}")
    public List<CropBooking> getBuyerBookingsByEmail(
            @PathVariable String email) {
        return bookingService.getBuyerBookingsByEmail(email);
    }

    @GetMapping("/farmer/{farmerId}")
    public List<CropBooking> getFarmerBookings(
            @PathVariable Long farmerId) {
        return bookingService.getFarmerBookings(farmerId);
    }

    @GetMapping("/farmer/email/{email}")
    public List<CropBooking> getFarmerBookingsByEmail(
            @PathVariable String email) {
        return bookingService.getFarmerBookingsByEmail(email);
    }

    @GetMapping("/crop/{cropId}")
    public List<CropBooking> getCropBookings(
            @PathVariable Integer cropId) {
        return bookingService.getCropBookings(cropId);
    }

    @PutMapping("/{bookingId}/accept")
    public ResponseEntity<?> acceptBooking(
            @PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(
                    bookingService.acceptBooking(bookingId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");

            if (status == null || status.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Status is required"));
            }

            return ResponseEntity.ok(
                    bookingService.updateBookingStatus(bookingId, status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<?> editBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, Object> request) {
        try {
            Object emailValue = request.get("buyerEmail");
            Object quantityValue = request.get("quantity");

            if (emailValue == null || quantityValue == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message",
                                "Buyer email and quantity are required"));
            }

            Double quantity = Double.valueOf(quantityValue.toString());

            return ResponseEntity.ok(
                    bookingService.editBuyerBooking(
                            bookingId, emailValue.toString(), quantity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> deleteBooking(
            @PathVariable Long bookingId,
            @RequestParam String buyerEmail) {
        try {
            bookingService.deleteBuyerBooking(bookingId, buyerEmail);

            return ResponseEntity.ok(
                    Map.of("message", "Booking deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
