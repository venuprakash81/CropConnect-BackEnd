package com.example.demo.controller;

import com.example.demo.entity.WarehouseBooking;
import com.example.demo.service.WarehouseBookingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse-bookings")
@CrossOrigin(origins = "http://localhost:3000")
public class WarehouseBookingController {

    @Autowired
    private WarehouseBookingService warehouseBookingService;


    // =========================================================
    // CREATE BOOKING FOR A WAREHOUSE
    // =========================================================

    @PostMapping("/warehouse/{warehouseId}")
    public ResponseEntity<?> createBooking(
            @PathVariable Long warehouseId,
            @RequestBody WarehouseBooking booking) {

        try {

            WarehouseBooking createdBooking =
                    warehouseBookingService.createBooking(
                            warehouseId,
                            booking
                    );

            return ResponseEntity.ok(createdBooking);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // GET ALL BOOKINGS
    // =========================================================

    @GetMapping("/all")
    public ResponseEntity<List<WarehouseBooking>> getAllBookings() {

        List<WarehouseBooking> bookings =
                warehouseBookingService.getAllBookings();

        return ResponseEntity.ok(bookings);
    }


    // =========================================================
    // GET SINGLE BOOKING
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(
            @PathVariable Long id) {

        try {

            WarehouseBooking booking =
                    warehouseBookingService.getBookingById(id);

            return ResponseEntity.ok(booking);

        } catch (Exception e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }


    // =========================================================
    // GET BOOKINGS BY WAREHOUSE
    // IMPORTANT FOR MY WAREHOUSE BOOKINGS PAGE
    // =========================================================

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<WarehouseBooking>> getBookingsByWarehouse(
            @PathVariable Long warehouseId) {

        List<WarehouseBooking> bookings =
                warehouseBookingService.getBookingsByWarehouse(
                        warehouseId
                );

        return ResponseEntity.ok(bookings);
    }


    // =========================================================
    // GET BOOKINGS BY CUSTOMER EMAIL
    // =========================================================

    @GetMapping("/customer/{email}")
    public ResponseEntity<List<WarehouseBooking>> getBookingsByCustomer(
            @PathVariable String email) {

        List<WarehouseBooking> bookings =
                warehouseBookingService.getBookingsByCustomer(
                        email
                );

        return ResponseEntity.ok(bookings);
    }


    // =========================================================
    // UPDATE COMPLETE BOOKING
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(
            @PathVariable Long id,
            @RequestBody WarehouseBooking booking) {

        try {

            WarehouseBooking updatedBooking =
                    warehouseBookingService.createBooking(
                            id,
                            booking
                    );

            return ResponseEntity.ok(updatedBooking);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // UPDATE BOOKING STATUS
    //
    // Example:
    // PUT /api/warehouse-bookings/1/status?status=APPROVED
    //
    // APPROVED
    // REJECTED
    // PENDING
    // =========================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {

            WarehouseBooking updatedBooking =
                    warehouseBookingService.updateStatus(
                            id,
                            status
                    );

            return ResponseEntity.ok(updatedBooking);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // UPDATE PAYMENT STATUS
    //
    // Example:
    // PUT /api/warehouse-bookings/1/payment-status?paymentStatus=PAID
    // =========================================================

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String paymentStatus) {

        try {

            WarehouseBooking updatedBooking =
                    warehouseBookingService.updatePaymentStatus(
                            id,
                            paymentStatus
                    );

            return ResponseEntity.ok(updatedBooking);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // DELETE BOOKING
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(
            @PathVariable Long id) {

        try {

            warehouseBookingService.deleteBooking(id);

            return ResponseEntity.ok(
                    "Warehouse booking deleted successfully"
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}