package com.example.demo.dto;

public class CropBookingRequest {

    private Integer cropId;
    private Long buyerId;
    private String buyerName;
    private String buyerEmail;
    private Double quantity;

    public Integer getCropId() { return cropId; }
    public void setCropId(Integer cropId) { this.cropId = cropId; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}