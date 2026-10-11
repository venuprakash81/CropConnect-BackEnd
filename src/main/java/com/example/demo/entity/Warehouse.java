package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String warehouseName;
    private String ownerName;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    private String phone;
    private String address;
    private String city;
    private String state;

    private Double latitude;
    private Double longitude;

    private Double totalStorageCapacity;
    private String supportedCrops;

    private String profilePhoto;
    private String warehouseDocument;

    private boolean coldStorage;
    private boolean loadingFacility;
    private boolean securityAvailable;

    public Warehouse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getTotalStorageCapacity() {
        return totalStorageCapacity;
    }

    public void setTotalStorageCapacity(Double totalStorageCapacity) {
        this.totalStorageCapacity = totalStorageCapacity;
    }

    public String getSupportedCrops() {
        return supportedCrops;
    }

    public void setSupportedCrops(String supportedCrops) {
        this.supportedCrops = supportedCrops;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getWarehouseDocument() {
        return warehouseDocument;
    }

    public void setWarehouseDocument(String warehouseDocument) {
        this.warehouseDocument = warehouseDocument;
    }

    public boolean isColdStorage() {
        return coldStorage;
    }

    public void setColdStorage(boolean coldStorage) {
        this.coldStorage = coldStorage;
    }

    public boolean isLoadingFacility() {
        return loadingFacility;
    }

    public void setLoadingFacility(boolean loadingFacility) {
        this.loadingFacility = loadingFacility;
    }

    public boolean isSecurityAvailable() {
        return securityAvailable;
    }

    public void setSecurityAvailable(boolean securityAvailable) {
        this.securityAvailable = securityAvailable;
    }
}