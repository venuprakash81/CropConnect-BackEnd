package com.example.demo.controller;

import com.example.demo.entity.TransportBooking;
import com.example.demo.service.TransportBookingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transport-bookings")
@CrossOrigin(
        origins = {
                "https://cropconnect-venuprakash.vercel.app"
        }
)
public class TransportBookingController {

    @Autowired
    private TransportBookingService transportBookingService;

    // =====================================================
    // CREATE BOOKING FOR TRANSPORTER
    // =====================================================

    @PostMapping("/transporter/{transporterId}")
    public ResponseEntity<TransportBooking> createBooking(
            @PathVariable Long transporterId,
            @RequestBody TransportBooking booking) {

        return ResponseEntity.ok(
                transportBookingService.createBooking(
                        transporterId,
                        booking
                )
        );
    }

    // =====================================================
    // GET ONLY THIS TRANSPORTER'S BOOKINGS
    // =====================================================

    @GetMapping("/transporter/{transporterId}")
    public ResponseEntity<List<TransportBooking>>
    getTransporterBookings(
            @PathVariable Long transporterId) {

        return ResponseEntity.ok(
                transportBookingService
                        .getBookingsByTransporter(
                                transporterId
                        )
        );
    }

    // =====================================================
    // GET ONE BOOKING
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<TransportBooking> getBooking(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                transportBookingService.getBooking(id)
        );
    }

    // =====================================================
    // GET ALL BOOKINGS
    // =====================================================

    @GetMapping("/all")
    public ResponseEntity<List<TransportBooking>>
    getAllBookings() {

        return ResponseEntity.ok(
                transportBookingService.getAllBookings()
        );
    }

    // =====================================================
    // UPDATE BOOKING
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<TransportBooking> updateBooking(
            @PathVariable Long id,
            @RequestBody TransportBooking booking) {

        return ResponseEntity.ok(
                transportBookingService.updateBooking(
                        id,
                        booking
                )
        );
    }

    // =====================================================
    // DELETE BOOKING
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(
            @PathVariable Long id) {

        transportBookingService.deleteBooking(id);

        return ResponseEntity.ok(
                "Transport booking deleted successfully"
        );
    }
}
