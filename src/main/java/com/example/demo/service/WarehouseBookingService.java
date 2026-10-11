package com.example.demo.service;

import com.example.demo.entity.Warehouse;
import com.example.demo.entity.WarehouseBooking;
import com.example.demo.repository.WarehouseBookingRepository;
import com.example.demo.repository.WarehouseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WarehouseBookingService {

    private final WarehouseBookingRepository bookingRepository;
    private final WarehouseRepository warehouseRepository;

    public WarehouseBookingService(
            WarehouseBookingRepository bookingRepository,
            WarehouseRepository warehouseRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public WarehouseBooking createBooking(
            Long warehouseId,
            WarehouseBooking booking
    ) {

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Warehouse not found with ID: " + warehouseId
                        )
                );

        if (booking.getCustomerName() == null ||
                booking.getCustomerName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Customer name is required"
            );
        }

        if (booking.getCustomerEmail() == null ||
                booking.getCustomerEmail().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Customer email is required"
            );
        }

        if (booking.getCustomerPhone() == null ||
                !booking.getCustomerPhone().matches("\\d{10}")) {

            throw new IllegalArgumentException(
                    "Phone number must contain 10 digits"
            );
        }

        if (booking.getCropName() == null ||
                booking.getCropName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Crop name is required"
            );
        }

        if (booking.getQuantity() == null ||
                booking.getQuantity() <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (booking.getStartDate() == null ||
                booking.getEndDate() == null) {

            throw new IllegalArgumentException(
                    "Start date and end date are required"
            );
        }

        if (booking.getEndDate()
                .isBefore(booking.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }

        booking.setWarehouseId(warehouse.getId());
        booking.setWarehouseName(warehouse.getWarehouseName());

        if (booking.getCustomerType() == null ||
                booking.getCustomerType().trim().isEmpty()) {

            booking.setCustomerType("FARMER");
        }

        booking.setStatus("PENDING");

        if (booking.getPaymentStatus() == null ||
                booking.getPaymentStatus().trim().isEmpty()) {

            booking.setPaymentStatus("NOT_PAID");
        }

        if (booking.getAmount() == null) {
            booking.setAmount(0.0);
        }

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<WarehouseBooking> getAllBookings() {

        return bookingRepository.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public WarehouseBooking getBookingById(Long id) {

        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking not found with ID: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<WarehouseBooking> getBookingsByCustomer(
            String email
    ) {

        return bookingRepository
                .findByCustomerEmailOrderByIdDesc(email);
    }

    @Transactional(readOnly = true)
    public List<WarehouseBooking> getBookingsByWarehouse(
            Long warehouseId
    ) {

        return bookingRepository
                .findByWarehouseIdOrderByIdDesc(warehouseId);
    }

    public WarehouseBooking updateStatus(
            Long id,
            String status
    ) {

        WarehouseBooking booking = getBookingById(id);

        booking.setStatus(status);

        return bookingRepository.save(booking);
    }

    public WarehouseBooking updatePaymentStatus(
            Long id,
            String paymentStatus
    ) {

        WarehouseBooking booking = getBookingById(id);

        booking.setPaymentStatus(paymentStatus);

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {

        if (!bookingRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Booking not found with ID: " + id
            );
        }

        bookingRepository.deleteById(id);
    }
}