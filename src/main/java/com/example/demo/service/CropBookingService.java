package com.example.demo.service;

import com.example.demo.dto.CropBookingRequest;
import com.example.demo.entity.Buyer;
import com.example.demo.entity.Crop;
import com.example.demo.entity.CropBooking;
import com.example.demo.entity.Farmer;
import com.example.demo.repository.BuyerRepository;
import com.example.demo.repository.CropBookingRepository;
import com.example.demo.repository.CropRepository;
import com.example.demo.repository.FarmerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class CropBookingService {

    private final CropBookingRepository bookingRepository;
    private final CropRepository cropRepository;
    private final FarmerRepository farmerRepository;
    private final BuyerRepository buyerRepository;

    public CropBookingService(
            CropBookingRepository bookingRepository,
            CropRepository cropRepository,
            FarmerRepository farmerRepository,
            BuyerRepository buyerRepository) {
        this.bookingRepository = bookingRepository;
        this.cropRepository = cropRepository;
        this.farmerRepository = farmerRepository;
        this.buyerRepository = buyerRepository;
    }

    @Transactional
    public CropBooking bookCrop(CropBookingRequest request) {
        if (request == null
                || request.getCropId() == null
                || request.getBuyerId() == null
                || request.getQuantity() == null
                || !Double.isFinite(request.getQuantity())
                || request.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Crop ID, buyer ID and positive quantity are required");
        }

        // Verify buyer exists and use the registered email from the database.
        Buyer buyer = buyerRepository.findById(request.getBuyerId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Buyer not found"));

        if (request.getBuyerEmail() == null
                || !request.getBuyerEmail().trim()
                        .equalsIgnoreCase(buyer.getEmail())) {
            throw new IllegalArgumentException(
                    "Buyer email does not match the registered buyer");
        }

        Crop crop = cropRepository.findByIdForUpdate(request.getCropId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Crop not found"));

        if (crop.getFarmerId() == null) {
            throw new IllegalArgumentException("Crop has no assigned farmer");
        }

        Farmer farmer = farmerRepository.findById(crop.getFarmerId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Farmer not found"));

        if (crop.getQuantity() == null
                || request.getQuantity() > crop.getQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity is not available");
        }

        if (crop.getPrice() == null
                || !Double.isFinite(crop.getPrice())
                || crop.getPrice() <= 0) {
            throw new IllegalArgumentException("Invalid crop price");
        }

        double total = crop.getPrice() * request.getQuantity();

        if (!Double.isFinite(total)) {
            throw new IllegalArgumentException("Invalid booking amount");
        }

        CropBooking booking = new CropBooking();
        booking.setCropId(crop.getId());
        booking.setFarmerId(farmer.getId());
        booking.setFarmerEmail(farmer.getEmail());
        booking.setBuyerId(buyer.getId());
        booking.setBuyerName(
                request.getBuyerName() == null
                        ? "" : request.getBuyerName().trim());
        booking.setBuyerEmail(buyer.getEmail());
        booking.setQuantity(request.getQuantity());
        booking.setUnit(crop.getUnit());
        booking.setPrice(crop.getPrice());
        booking.setPricePerUnit(crop.getPrice());
        booking.setTotalPrice(total);
        booking.setStatus("PENDING");
        booking.setPaymentStatus("PENDING");

        // Reserve stock atomically within the transaction.
        crop.setQuantity(crop.getQuantity() - request.getQuantity());
        cropRepository.save(crop);

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<CropBooking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CropBooking> getBuyerBookings(Long buyerId) {
        return bookingRepository.findByBuyerId(buyerId);
    }

    @Transactional(readOnly = true)
    public List<CropBooking> getBuyerBookingsByEmail(String email) {
        return bookingRepository.findByBuyerEmailIgnoreCase(email.trim());
    }

    @Transactional(readOnly = true)
    public List<CropBooking> getFarmerBookings(Long farmerId) {
        return bookingRepository.findByFarmerId(farmerId);
    }

    @Transactional(readOnly = true)
    public List<CropBooking> getFarmerBookingsByEmail(String email) {
        return bookingRepository.findByFarmerEmailIgnoreCase(email.trim());
    }

    @Transactional(readOnly = true)
    public List<CropBooking> getCropBookings(Integer cropId) {
        return bookingRepository.findByCropId(cropId);
    }

    @Transactional
    public CropBooking acceptBooking(Long bookingId) {
        CropBooking booking = findBooking(bookingId);
        requirePending(booking);
        booking.setStatus("ACCEPTED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public CropBooking updateBookingStatus(Long bookingId, String status) {
        CropBooking booking = findBooking(bookingId);

        String normalized = status.trim().toUpperCase(Locale.ROOT);

        if (!normalized.equals("ACCEPTED")
                && !normalized.equals("REJECTED")
                && !normalized.equals("COMPLETED")) {
            throw new IllegalArgumentException("Invalid booking status");
        }

        requirePending(booking);

        if ("REJECTED".equals(normalized)) {
            restoreStock(booking);
        }

        booking.setStatus(normalized);
        return bookingRepository.save(booking);
    }

    @Transactional
    public CropBooking editBuyerBooking(
            Long bookingId, String buyerEmail, Double quantity) {

        CropBooking booking = findBooking(bookingId);
        verifyBuyerEmail(booking, buyerEmail);
        requireEditable(booking);
        validateQuantity(quantity);

        Crop crop = cropRepository.findByIdForUpdate(booking.getCropId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Crop not found"));

        double available = crop.getQuantity() + booking.getQuantity();

        if (quantity > available) {
            throw new IllegalArgumentException(
                    "Requested quantity is not available");
        }

        double total = booking.getPricePerUnit() * quantity;

        if (!Double.isFinite(total)) {
            throw new IllegalArgumentException("Invalid booking amount");
        }

        crop.setQuantity(available - quantity);
        cropRepository.save(crop);

        booking.setQuantity(quantity);
        booking.setTotalPrice(total);

        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBuyerBooking(Long bookingId, String buyerEmail) {
        CropBooking booking = findBooking(bookingId);
        verifyBuyerEmail(booking, buyerEmail);
        requireEditable(booking);

        restoreStock(booking);
        bookingRepository.delete(booking);
    }

    private CropBooking findBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));
    }

    private void requirePending(CropBooking booking) {
        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException(
                    "Only pending bookings can change status");
        }
    }

    private void requireEditable(CropBooking booking) {
        if (!"PENDING".equalsIgnoreCase(booking.getStatus())
                || !"PENDING".equalsIgnoreCase(booking.getPaymentStatus())) {
            throw new IllegalArgumentException(
                    "Only unpaid pending bookings can be edited or deleted");
        }
    }

    private void validateQuantity(Double quantity) {
        if (quantity == null || !Double.isFinite(quantity) || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be a positive number");
        }
    }

    private void verifyBuyerEmail(
            CropBooking booking, String buyerEmail) {
        if (buyerEmail == null || booking.getBuyerEmail() == null
                || !booking.getBuyerEmail()
                        .equalsIgnoreCase(buyerEmail.trim())) {
            throw new IllegalArgumentException(
                    "Booking does not belong to this buyer");
        }
    }

    private void restoreStock(CropBooking booking) {
        Crop crop = cropRepository.findByIdForUpdate(booking.getCropId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Crop not found"));

        double current = crop.getQuantity() == null ? 0 : crop.getQuantity();
        crop.setQuantity(current + booking.getQuantity());
        cropRepository.save(crop);
    }
}