package com.example.demo.repository;

import com.example.demo.entity.TransportBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransportBookingRepository
        extends JpaRepository<TransportBooking, Long> {

    // Get only bookings belonging to one transporter
    List<TransportBooking> findByTransporterIdOrderByIdDesc(Long transporterId);

    // Optional: all bookings
    List<TransportBooking> findAllByOrderByIdDesc();
}