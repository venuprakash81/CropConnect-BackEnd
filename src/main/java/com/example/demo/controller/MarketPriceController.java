package com.example.demo.controller;

import com.example.demo.entity.MarketPrice;
import com.example.demo.service.MarketPriceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market-prices")
@CrossOrigin(origins = "https://cropconnect-venuprakash.vercel.app")
public class MarketPriceController {

    private final MarketPriceService service;

    public MarketPriceController(MarketPriceService service) {
        this.service = service;
    }

    // Add market price
    @PostMapping
    public ResponseEntity<?> addPrice(
            @RequestBody MarketPrice price
    ) {
        try {
            return ResponseEntity.ok(
                    service.addPrice(price)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    // Get all market prices
    @GetMapping
    public ResponseEntity<List<MarketPrice>> getAllPrices() {
        return ResponseEntity.ok(
                service.getAllPrices()
        );
    }

    // Get market price by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPriceById(
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(
                    service.getPriceById(id)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    // Update market price
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrice(
            @PathVariable Long id,
            @RequestBody MarketPrice price
    ) {
        try {
            return ResponseEntity.ok(
                    service.updatePrice(id, price)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );
        }
    }

    // Delete market price
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrice(
            @PathVariable Long id
    ) {
        try {
            service.deletePrice(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Market price deleted successfully"
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    Map.of("message", e.getMessage())
            );
        }
    }
}
