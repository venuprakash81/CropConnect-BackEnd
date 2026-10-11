package com.example.demo.repository;

import com.example.demo.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {

    Optional<Buyer> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}