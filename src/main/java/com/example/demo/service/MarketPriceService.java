package com.example.demo.service;

import com.example.demo.entity.MarketPrice;
import com.example.demo.repository.MarketPriceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MarketPriceService {

    private final MarketPriceRepository repository;

    public MarketPriceService(MarketPriceRepository repository) {
        this.repository = repository;
    }

    // Add a market price
    public MarketPrice addPrice(MarketPrice price) {
        validate(price);

        if (price.getPriceDate() == null) {
            price.setPriceDate(LocalDate.now());
        }

        return repository.save(price);
    }

    // Get all market prices
    @Transactional(readOnly = true)
    public List<MarketPrice> getAllPrices() {
        return repository.findAll();
    }

    // Get one market price
    @Transactional(readOnly = true)
    public MarketPrice getPriceById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Market price not found: " + id
                        )
                );
    }

    // Update a market price
    public MarketPrice updatePrice(
            Long id,
            MarketPrice updated
    ) {
        validate(updated);

        MarketPrice existing = getPriceById(id);

        existing.setCropName(updated.getCropName());
        existing.setMarketName(updated.getMarketName());
        existing.setLocation(updated.getLocation());
        existing.setPricePerQuintal(
                updated.getPricePerQuintal()
        );
        existing.setPriceDate(
                updated.getPriceDate() != null
                        ? updated.getPriceDate()
                        : LocalDate.now()
        );
        existing.setQuality(updated.getQuality());

        return repository.save(existing);
    }

    // Delete a market price
    public void deletePrice(Long id) {
        MarketPrice existing = getPriceById(id);
        repository.delete(existing);
    }

    // Validate input
    private void validate(MarketPrice price) {
        if (price == null) {
            throw new IllegalArgumentException(
                    "Market price details are required"
            );
        }

        if (price.getCropName() == null
                || price.getCropName().isBlank()) {
            throw new IllegalArgumentException(
                    "Crop name is required"
            );
        }

        if (price.getMarketName() == null
                || price.getMarketName().isBlank()) {
            throw new IllegalArgumentException(
                    "Market name is required"
            );
        }

        if (price.getLocation() == null
                || price.getLocation().isBlank()) {
            throw new IllegalArgumentException(
                    "Location is required"
            );
        }

        if (price.getPricePerQuintal() == null
                || !Double.isFinite(price.getPricePerQuintal())
                || price.getPricePerQuintal() <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero"
            );
        }
    }
}