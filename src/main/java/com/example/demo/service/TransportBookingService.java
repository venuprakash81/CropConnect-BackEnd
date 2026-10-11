package com.example.demo.service;

import com.example.demo.entity.TransportBooking;
import com.example.demo.entity.Transporter;
import com.example.demo.repository.TransportBookingRepository;
import com.example.demo.repository.TransporterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportBookingService {

    @Autowired
    private TransportBookingRepository transportBookingRepository;

    @Autowired
    private TransporterRepository transporterRepository;

    // =====================================================
    // CREATE BOOKING
    // =====================================================

    public TransportBooking createBooking(
            Long transporterId,
            TransportBooking booking) {

        if (transporterId == null) {
            throw new RuntimeException("Transporter ID is required");
        }

        Transporter transporter =
                transporterRepository.findById(transporterId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transporter not found with ID: "
                                                + transporterId));

        if (booking.getUserType() == null ||
                booking.getUserType().trim().isEmpty()) {

            throw new RuntimeException("User type is required");
        }

        if (booking.getPickupLatitude() == null ||
                booking.getPickupLongitude() == null) {

            throw new RuntimeException(
                    "Pickup location is required");
        }

        if (booking.getDestinationLatitude() == null ||
                booking.getDestinationLongitude() == null) {

            throw new RuntimeException(
                    "Destination location is required");
        }

        validateCoordinates(
                booking.getPickupLatitude(),
                booking.getPickupLongitude()
        );

        validateCoordinates(
                booking.getDestinationLatitude(),
                booking.getDestinationLongitude()
        );

        booking.setTransporter(transporter);

        if (booking.getStatus() == null ||
                booking.getStatus().trim().isEmpty()) {

            booking.setStatus("PENDING");
        }

        return transportBookingRepository.save(booking);
    }

    // =====================================================
    // GET ALL BOOKINGS OF ONE TRANSPORTER
    // =====================================================

    public List<TransportBooking> getBookingsByTransporter(
            Long transporterId) {

        if (transporterId == null) {
            throw new RuntimeException("Transporter ID is required");
        }

        if (!transporterRepository.existsById(transporterId)) {
            throw new RuntimeException(
                    "Transporter not found with ID: "
                            + transporterId);
        }

        return transportBookingRepository
                .findByTransporterIdOrderByIdDesc(transporterId);
    }

    // =====================================================
    // GET ONE BOOKING
    // =====================================================

    public TransportBooking getBooking(Long id) {

        return transportBookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Transport booking not found with ID: "
                                        + id));
    }

    // =====================================================
    // GET ALL BOOKINGS
    // =====================================================

    public List<TransportBooking> getAllBookings() {

        return transportBookingRepository
                .findAllByOrderByIdDesc();
    }

    // =====================================================
    // UPDATE BOOKING
    // =====================================================

    public TransportBooking updateBooking(
            Long id,
            TransportBooking updatedBooking) {

        TransportBooking existing =
                transportBookingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transport booking not found"));

        existing.setUserType(updatedBooking.getUserType());
        existing.setUserId(updatedBooking.getUserId());
        existing.setUserName(updatedBooking.getUserName());
        existing.setUserEmail(updatedBooking.getUserEmail());
        existing.setUserPhone(updatedBooking.getUserPhone());

        existing.setPickupLatitude(
                updatedBooking.getPickupLatitude());

        existing.setPickupLongitude(
                updatedBooking.getPickupLongitude());

        existing.setDestinationLatitude(
                updatedBooking.getDestinationLatitude());

        existing.setDestinationLongitude(
                updatedBooking.getDestinationLongitude());

        existing.setGoodsName(
                updatedBooking.getGoodsName());

        existing.setQuantity(
                updatedBooking.getQuantity());

        existing.setQuantityUnit(
                updatedBooking.getQuantityUnit());

        existing.setBookingDate(
                updatedBooking.getBookingDate());

        existing.setNotes(
                updatedBooking.getNotes());

        if (updatedBooking.getStatus() != null) {
            existing.setStatus(
                    updatedBooking.getStatus());
        }

        return transportBookingRepository.save(existing);
    }

    // =====================================================
    // DELETE BOOKING
    // =====================================================

    public void deleteBooking(Long id) {

        if (!transportBookingRepository.existsById(id)) {
            throw new RuntimeException(
                    "Transport booking not found");
        }

        transportBookingRepository.deleteById(id);
    }

    // =====================================================
    // VALIDATE COORDINATES
    // =====================================================

    private void validateCoordinates(
            Double latitude,
            Double longitude) {

        if (latitude < -90 || latitude > 90) {
            throw new RuntimeException(
                    "Invalid latitude");
        }

        if (longitude < -180 || longitude > 180) {
            throw new RuntimeException(
                    "Invalid longitude");
        }
    }
}