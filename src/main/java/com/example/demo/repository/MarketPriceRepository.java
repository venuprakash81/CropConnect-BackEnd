package com.example.demo.repository;

import com.example.demo.entity.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketPriceRepository
        extends JpaRepository<MarketPrice, Long> {

    List<MarketPrice> findByCropNameContainingIgnoreCase(
            String cropName
    );

    List<MarketPrice> findByLocationContainingIgnoreCase(
            String location
    );

    List<MarketPrice> findByMarketNameContainingIgnoreCase(
            String marketName
    );
}