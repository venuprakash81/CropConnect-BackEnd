package com.example.demo.repository;

import com.example.demo.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    Optional<Farmer> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}