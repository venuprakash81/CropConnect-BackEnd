package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "market_prices")
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "crop", nullable = false)
    private String cropName;

    @Column(name = "market_name", nullable = false)
    private String marketName;

    @Column(name = "location", nullable = false)
    private String location;

    // Maps the Java property to the existing MySQL column "price"
    @Column(name = "price", nullable = false)
    private Double pricePerQuintal;

    @Column(name = "price_date")
    private LocalDate priceDate;

    @Column(name = "quality")
    private String quality;

    public MarketPrice() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getPricePerQuintal() {
        return pricePerQuintal;
    }

    public void setPricePerQuintal(Double pricePerQuintal) {
        this.pricePerQuintal = pricePerQuintal;
    }

    public LocalDate getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(LocalDate priceDate) {
        this.priceDate = priceDate;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}