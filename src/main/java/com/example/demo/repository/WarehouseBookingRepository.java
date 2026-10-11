package com.example.demo.repository;

import com.example.demo.entity.WarehouseBooking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseBookingRepository
        extends JpaRepository<WarehouseBooking, Long> {

    List<WarehouseBooking>
    findByCustomerEmailOrderByIdDesc(String customerEmail);

    List<WarehouseBooking>
    findByWarehouseIdOrderByIdDesc(Long warehouseId);

    List<WarehouseBooking>
    findAllByOrderByIdDesc();
}