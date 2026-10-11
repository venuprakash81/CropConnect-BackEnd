package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transport_bookings")
public class TransportBooking {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // TRANSPORTER
    // =====================================================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transporter_id", nullable = false)
    private Transporter transporter;

    // =====================================================
    // USER DETAILS
    // =====================================================

    @Column(nullable = false)
    private String userType;

    private Long userId;

    private String userName;

    private String userEmail;

    private String userPhone;

    // =====================================================
    // PICKUP LOCATION
    // =====================================================

    @Column(name = "pickup_latitude")
    private Double pickupLatitude;

    @Column(name = "pickup_longitude")
    private Double pickupLongitude;

    // =====================================================
    // DESTINATION LOCATION
    // =====================================================

    @Column(name = "destination_latitude")
    private Double destinationLatitude;

    @Column(name = "destination_longitude")
    private Double destinationLongitude;

    // =====================================================
    // GOODS
    // =====================================================

    private String goodsName;

    private Double quantity;

    private String quantityUnit;

    // =====================================================
    // BOOKING
    // =====================================================

    private LocalDate bookingDate;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private String status = "PENDING";

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public TransportBooking() {
    }

    // =====================================================
    // ID GETTER / SETTER
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // =====================================================
    // TRANSPORTER
    // =====================================================

    public Transporter getTransporter() {
        return transporter;
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    // =====================================================
    // USER TYPE
    // =====================================================

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    // =====================================================
    // USER ID
    // =====================================================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // =====================================================
    // USER NAME
    // =====================================================

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // =====================================================
    // USER EMAIL
    // =====================================================

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    // =====================================================
    // USER PHONE
    // =====================================================

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    // =====================================================
    // PICKUP LATITUDE
    // =====================================================

    public Double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    // =====================================================
    // PICKUP LONGITUDE
    // =====================================================

    public Double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    // =====================================================
    // DESTINATION LATITUDE
    // =====================================================

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(
            Double destinationLatitude) {

        this.destinationLatitude =
                destinationLatitude;
    }

    // =====================================================
    // DESTINATION LONGITUDE
    // =====================================================

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(
            Double destinationLongitude) {

        this.destinationLongitude =
                destinationLongitude;
    }

    // =====================================================
    // GOODS NAME
    // =====================================================

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    // =====================================================
    // QUANTITY
    // =====================================================

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    // =====================================================
    // QUANTITY UNIT
    // =====================================================

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    // =====================================================
    // BOOKING DATE
    // =====================================================

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    // =====================================================
    // NOTES
    // =====================================================

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // =====================================================
    // STATUS
    // =====================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}