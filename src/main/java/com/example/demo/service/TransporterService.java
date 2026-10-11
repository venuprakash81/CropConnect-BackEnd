
package com.example.demo.service;

import com.example.demo.entity.Transporter;
import com.example.demo.repository.TransporterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransporterService {

    private final TransporterRepository repository;

    public TransporterService(TransporterRepository repository) {
        this.repository = repository;
    }

    public Transporter registerTransporter(Transporter t) {
        if (repository.existsByEmail(t.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (repository.existsByPhone(t.getPhone())) {
            throw new IllegalArgumentException("Phone already registered");
        }

        if (t.getVehicleNumber() != null
                && !t.getVehicleNumber().isBlank()
                && repository.existsByVehicleNumber(t.getVehicleNumber())) {
            throw new IllegalArgumentException("Vehicle number already registered");
        }

        return repository.save(t);
    }

    public Transporter loginTransporter(String email, String password) {
        Transporter t = repository.findByEmail(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password"));

        // For compatibility with your existing plain-text passwords.
        // Migrate to BCrypt before production deployment.
        if (!t.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return t;
    }

    public Transporter getTransporterById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Transporter not found"));
    }

    public List<Transporter> getAllTransporters() {
        return repository.findAllByOrderByIdDesc();
    }

    public Map<String, Object> getProfileResponse(Long id) {
        return toSafeResponse(getTransporterById(id));
    }

    private Map<String, Object> toSafeResponse(Transporter t) {
        Map<String, Object> m = new HashMap<>();

        m.put("id", t.getId());
        m.put("fullName", t.getFullName());
        m.put("email", t.getEmail());
        m.put("phone", t.getPhone());
        m.put("alternativePhone", t.getAlternativePhone());
        m.put("profilePhoto", t.getProfilePhoto());
        m.put("dateOfBirth", t.getDateOfBirth());
        m.put("aadhaarNumber", t.getAadhaarNumber());
        m.put("drivingLicenceNumber", t.getDrivingLicenceNumber());

        m.put("businessName", t.getBusinessName());
        m.put("transporterType", t.getTransporterType());
        m.put("gstNumber", t.getGstNumber());
        m.put("yearsOfExperience", t.getYearsOfExperience());
        m.put("businessAddress", t.getBusinessAddress());

        m.put("village", t.getVillage());
        m.put("mandal", t.getMandal());
        m.put("district", t.getDistrict());
        m.put("state", t.getState());
        m.put("pincode", t.getPincode());

        m.put("latitude", t.getLatitude());
        m.put("longitude", t.getLongitude());

        m.put("vehicleNumber", t.getVehicleNumber());
        m.put("vehicleType", t.getVehicleType());
        m.put("vehicleModel", t.getVehicleModel());
        m.put("vehicleCapacity", t.getVehicleCapacity());
        m.put("capacityUnit", t.getCapacityUnit());
        m.put("vehicleOwnership", t.getVehicleOwnership());
        m.put("fuelType", t.getFuelType());
        m.put("coldStorageAvailable", t.getColdStorageAvailable());
        m.put("perishableGoodsSupported", t.getPerishableGoodsSupported());

        m.put("baseCharge", t.getBaseCharge());
        m.put("pricePerKm", t.getPricePerKm());
        m.put("pricePerQuintal", t.getPricePerQuintal());
        m.put("minimumTripCharge", t.getMinimumTripCharge());

        m.put("availableDays", t.getAvailableDays());
        m.put("availableTime", t.getAvailableTime());
        m.put("maximumTravelDistance", t.getMaximumTravelDistance());
        m.put("currentlyAvailable", t.getCurrentlyAvailable());

        m.put("drivingLicenceFile", t.getDrivingLicenceFile());
        m.put("vehicleRCFile", t.getVehicleRCFile());
        m.put("insuranceFile", t.getInsuranceFile());
        m.put("fitnessCertificateFile", t.getFitnessCertificateFile());
        m.put("pollutionCertificateFile", t.getPollutionCertificateFile());

        return m;
    }

    @Transactional
    public Map<String, Object> updateTransporterProfile(
            Long id, Transporter input) {

        Transporter t = getTransporterById(id);

        if (input.getFullName() == null || input.getFullName().isBlank()
                || input.getEmail() == null || input.getEmail().isBlank()
                || input.getPhone() == null
                || !input.getPhone().matches("\\d{10}")) {
            throw new IllegalArgumentException(
                    "Name, email and valid 10-digit phone are required");
        }

        repository.findByEmail(input.getEmail().trim())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Email already belongs to another transporter");
                });

        if (input.getVehicleNumber() != null
                && !input.getVehicleNumber().isBlank()) {
            repository.findAll().stream()
                    .filter(existing -> !existing.getId().equals(id))
                    .filter(existing -> input.getVehicleNumber()
                            .equalsIgnoreCase(existing.getVehicleNumber()))
                    .findFirst()
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException(
                                "Vehicle number already registered");
                    });
        }

        // Do not change ID, password, profile photo or document filenames.
        t.setFullName(input.getFullName().trim());
        t.setEmail(input.getEmail().trim());
        t.setPhone(input.getPhone().trim());
        t.setAlternativePhone(input.getAlternativePhone());
        t.setDateOfBirth(input.getDateOfBirth());
        t.setAadhaarNumber(input.getAadhaarNumber());
        t.setDrivingLicenceNumber(input.getDrivingLicenceNumber());

        t.setBusinessName(input.getBusinessName());
        t.setTransporterType(input.getTransporterType());
        t.setGstNumber(input.getGstNumber());
        t.setYearsOfExperience(input.getYearsOfExperience());
        t.setBusinessAddress(input.getBusinessAddress());

        t.setVillage(input.getVillage());
        t.setMandal(input.getMandal());
        t.setDistrict(input.getDistrict());
        t.setState(input.getState());
        t.setPincode(input.getPincode());

        t.setLatitude(input.getLatitude());
        t.setLongitude(input.getLongitude());

        t.setVehicleNumber(input.getVehicleNumber());
        t.setVehicleType(input.getVehicleType());
        t.setVehicleModel(input.getVehicleModel());
        t.setVehicleCapacity(input.getVehicleCapacity());
        t.setCapacityUnit(input.getCapacityUnit());
        t.setVehicleOwnership(input.getVehicleOwnership());
        t.setFuelType(input.getFuelType());
        t.setColdStorageAvailable(input.getColdStorageAvailable());
        t.setPerishableGoodsSupported(input.getPerishableGoodsSupported());

        t.setBaseCharge(input.getBaseCharge());
        t.setPricePerKm(input.getPricePerKm());
        t.setPricePerQuintal(input.getPricePerQuintal());
        t.setMinimumTripCharge(input.getMinimumTripCharge());

        t.setAvailableDays(input.getAvailableDays());
        t.setAvailableTime(input.getAvailableTime());
        t.setMaximumTravelDistance(input.getMaximumTravelDistance());
        t.setCurrentlyAvailable(input.getCurrentlyAvailable());

        return toSafeResponse(repository.save(t));
    }

    @Transactional
    public void changeTransporterPassword(
            Long id, String currentPassword, String newPassword) {

        Transporter t = getTransporterById(id);

        if (currentPassword == null
                || !t.getPassword().equals(currentPassword)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "New password must contain at least 8 characters");
        }

        if (newPassword.equals(currentPassword)) {
            throw new IllegalArgumentException(
                    "New password must differ from the current password");
        }

        t.setPassword(newPassword);
        repository.save(t);
    }

    @Transactional
    public Transporter updateLocation(Long id, Double latitude, Double longitude) {
        if (latitude == null || longitude == null
                || latitude < -90 || latitude > 90
                || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid GPS coordinates");
        }

        Transporter t = getTransporterById(id);
        t.setLatitude(latitude);
        t.setLongitude(longitude);
        return repository.save(t);
    }

    @Transactional
    public void deleteTransporter(Long id) {
        repository.delete(getTransporterById(id));
    }
}
