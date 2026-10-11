package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transporters")
public class Transporter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Personal Details

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    private String alternativePhone;

    private String profilePhoto;

    private LocalDate dateOfBirth;

    private String aadhaarNumber;

    private String drivingLicenceNumber;


    // Business Details

    private String businessName;

    private String transporterType;

    private String gstNumber;

    private Integer yearsOfExperience;

    @Column(length = 1000)
    private String businessAddress;


    // Address

    private String village;

    private String mandal;

    private String district;

    private String state;

    private String pincode;


    // Location

    private Double latitude;

    private Double longitude;


    // Vehicle

    private String vehicleNumber;

    private String vehicleType;

    private String vehicleModel;

    private Double vehicleCapacity;

    private String capacityUnit;

    private String vehicleOwnership;

    private String fuelType;

    private String coldStorageAvailable;

    private String perishableGoodsSupported;


    // Pricing

    private Double baseCharge;

    private Double pricePerKm;

    private Double pricePerQuintal;

    private Double minimumTripCharge;


    // Availability

    private String availableDays;

    private String availableTime;

    private Double maximumTravelDistance;

    private String currentlyAvailable;


    // Documents

    private String drivingLicenceFile;

    private String vehicleRCFile;

    private String insuranceFile;

    private String fitnessCertificateFile;

    private String pollutionCertificateFile;


    public Transporter() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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


    public String getAlternativePhone() {
        return alternativePhone;
    }

    public void setAlternativePhone(String alternativePhone) {
        this.alternativePhone = alternativePhone;
    }


    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }


    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }


    public String getDrivingLicenceNumber() {
        return drivingLicenceNumber;
    }

    public void setDrivingLicenceNumber(String drivingLicenceNumber) {
        this.drivingLicenceNumber =
                drivingLicenceNumber;
    }


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }


    public String getTransporterType() {
        return transporterType;
    }

    public void setTransporterType(String transporterType) {
        this.transporterType =
                transporterType;
    }


    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }


    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(
            Integer yearsOfExperience) {

        this.yearsOfExperience =
                yearsOfExperience;
    }


    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(
            String businessAddress) {

        this.businessAddress =
                businessAddress;
    }


    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }


    public String getMandal() {
        return mandal;
    }

    public void setMandal(String mandal) {
        this.mandal = mandal;
    }


    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
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


    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber =
                vehicleNumber;
    }


    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType =
                vehicleType;
    }


    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel =
                vehicleModel;
    }


    public Double getVehicleCapacity() {
        return vehicleCapacity;
    }

    public void setVehicleCapacity(
            Double vehicleCapacity) {

        this.vehicleCapacity =
                vehicleCapacity;
    }


    public String getCapacityUnit() {
        return capacityUnit;
    }

    public void setCapacityUnit(String capacityUnit) {
        this.capacityUnit =
                capacityUnit;
    }


    public String getVehicleOwnership() {
        return vehicleOwnership;
    }

    public void setVehicleOwnership(
            String vehicleOwnership) {

        this.vehicleOwnership =
                vehicleOwnership;
    }


    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }


    public String getColdStorageAvailable() {
        return coldStorageAvailable;
    }

    public void setColdStorageAvailable(
            String coldStorageAvailable) {

        this.coldStorageAvailable =
                coldStorageAvailable;
    }


    public String getPerishableGoodsSupported() {
        return perishableGoodsSupported;
    }

    public void setPerishableGoodsSupported(
            String perishableGoodsSupported) {

        this.perishableGoodsSupported =
                perishableGoodsSupported;
    }


    public Double getBaseCharge() {
        return baseCharge;
    }

    public void setBaseCharge(Double baseCharge) {
        this.baseCharge = baseCharge;
    }


    public Double getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(Double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }


    public Double getPricePerQuintal() {
        return pricePerQuintal;
    }

    public void setPricePerQuintal(
            Double pricePerQuintal) {

        this.pricePerQuintal =
                pricePerQuintal;
    }


    public Double getMinimumTripCharge() {
        return minimumTripCharge;
    }

    public void setMinimumTripCharge(
            Double minimumTripCharge) {

        this.minimumTripCharge =
                minimumTripCharge;
    }


    public String getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(
            String availableDays) {

        this.availableDays =
                availableDays;
    }


    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(
            String availableTime) {

        this.availableTime =
                availableTime;
    }


    public Double getMaximumTravelDistance() {
        return maximumTravelDistance;
    }

    public void setMaximumTravelDistance(
            Double maximumTravelDistance) {

        this.maximumTravelDistance =
                maximumTravelDistance;
    }


    public String getCurrentlyAvailable() {
        return currentlyAvailable;
    }

    public void setCurrentlyAvailable(
            String currentlyAvailable) {

        this.currentlyAvailable =
                currentlyAvailable;
    }


    public String getDrivingLicenceFile() {
        return drivingLicenceFile;
    }

    public void setDrivingLicenceFile(
            String drivingLicenceFile) {

        this.drivingLicenceFile =
                drivingLicenceFile;
    }


    public String getVehicleRCFile() {
        return vehicleRCFile;
    }

    public void setVehicleRCFile(
            String vehicleRCFile) {

        this.vehicleRCFile =
                vehicleRCFile;
    }


    public String getInsuranceFile() {
        return insuranceFile;
    }

    public void setInsuranceFile(
            String insuranceFile) {

        this.insuranceFile =
                insuranceFile;
    }


    public String getFitnessCertificateFile() {
        return fitnessCertificateFile;
    }

    public void setFitnessCertificateFile(
            String fitnessCertificateFile) {

        this.fitnessCertificateFile =
                fitnessCertificateFile;
    }


    public String getPollutionCertificateFile() {
        return pollutionCertificateFile;
    }

    public void setPollutionCertificateFile(
            String pollutionCertificateFile) {

        this.pollutionCertificateFile =
                pollutionCertificateFile;
    }
}