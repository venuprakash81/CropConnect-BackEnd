package com.example.demo.repository;

import com.example.demo.entity.CropBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropBookingRepository
        extends JpaRepository<CropBooking, Long> {

    List<CropBooking> findByBuyerId(Long buyerId);

    List<CropBooking> findByFarmerId(Long farmerId);

    List<CropBooking> findByCropId(Integer cropId);

    List<CropBooking> findByBuyerEmailIgnoreCase(String buyerEmail);

    List<CropBooking> findByFarmerEmailIgnoreCase(String farmerEmail);
}