package com.example.demo.controller;

import com.example.demo.entity.CropBooking;
import com.example.demo.entity.Farmer;
import com.example.demo.repository.CropBookingRepository;
import com.example.demo.repository.FarmerRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farmer-earnings")
@CrossOrigin(origins = "https://cropconnect-venuprakash.vercel.app")
public class FarmerEarningsController {

    private final FarmerRepository farmerRepository;
    private final CropBookingRepository bookingRepository;

    public FarmerEarningsController(
            FarmerRepository farmerRepository,
            CropBookingRepository bookingRepository) {
        this.farmerRepository = farmerRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getEarnings(@PathVariable String email) {

        Farmer farmer = farmerRepository.findByEmail(email)
                .orElse(null);

        if (farmer == null) {
            return ResponseEntity.notFound().build();
        }

        List<CropBooking> bookings =
                bookingRepository.findByFarmerId(farmer.getId());

        double totalEarnings = 0.0;
        double pendingAmount = 0.0;
        int successfulPayments = 0;
        int pendingPayments = 0;

        for (CropBooking booking : bookings) {
            double amount = booking.getTotalPrice() == null
                    ? 0.0
                    : booking.getTotalPrice();

            String paymentStatus = booking.getPaymentStatus();

            if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
                totalEarnings += amount;
                successfulPayments++;
            } else if ("PENDING".equalsIgnoreCase(paymentStatus)) {
                pendingAmount += amount;
                pendingPayments++;
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("farmerName", farmer.getFullName());
        response.put("email", farmer.getEmail());
        response.put("totalEarnings", totalEarnings);
        response.put("pendingAmount", pendingAmount);
        response.put("successfulPayments", successfulPayments);
        response.put("pendingPayments", pendingPayments);
        response.put("totalBookings", bookings.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{email}/payments")
    public ResponseEntity<?> getPaymentHistory(
            @PathVariable String email) {

        Farmer farmer = farmerRepository.findByEmail(email)
                .orElse(null);

        if (farmer == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                bookingRepository.findByFarmerId(farmer.getId())
        );
    }
}
