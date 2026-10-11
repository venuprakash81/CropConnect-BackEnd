
package com.example.demo.service;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.entity.Farmer;
import com.example.demo.repository.FarmerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FarmerService {

    private final FarmerRepository farmerRepository;

    public FarmerService(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    public Farmer registerFarmer(Farmer farmer) {
        return farmerRepository.save(farmer);
    }

    public List<Farmer> getAllFarmers() {
        return farmerRepository.findAll();
    }

    public Farmer getFarmerById(Long id) {
        return farmerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Farmer not found"));
    }

    public Farmer getFarmerByEmail(String email) {
        return farmerRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Farmer not found"));
    }

    @Transactional
    public Farmer updateFarmer(Long id, Farmer updatedFarmer) {
        Farmer existing = getFarmerById(id);

        existing.setFullName(updatedFarmer.getFullName());
        existing.setEmail(updatedFarmer.getEmail());
        existing.setPhone(updatedFarmer.getPhone());
        existing.setAlternativePhone(
                updatedFarmer.getAlternativePhone());

        if (updatedFarmer.getProfilePhoto() != null &&
                !updatedFarmer.getProfilePhoto().isBlank()) {
            existing.setProfilePhoto(
                    updatedFarmer.getProfilePhoto());
        }

        existing.setAadhaarNumber(
                updatedFarmer.getAadhaarNumber());
        existing.setDateOfBirth(
                updatedFarmer.getDateOfBirth());
        existing.setVillage(updatedFarmer.getVillage());
        existing.setMandal(updatedFarmer.getMandal());
        existing.setDistrict(updatedFarmer.getDistrict());
        existing.setState(updatedFarmer.getState());
        existing.setPincode(updatedFarmer.getPincode());
        existing.setLatitude(updatedFarmer.getLatitude());
        existing.setLongitude(updatedFarmer.getLongitude());
        existing.setTotalLand(updatedFarmer.getTotalLand());
        existing.setLandUnit(updatedFarmer.getLandUnit());

        // Do not replace the existing password during profile editing.
        return farmerRepository.save(existing);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        if (request == null ||
                request.getEmail() == null ||
                request.getCurrentPassword() == null ||
                request.getNewPassword() == null ||
                request.getConfirmPassword() == null) {
            throw new IllegalArgumentException(
                    "All password fields are required");
        }

        String email = request.getEmail().trim();

        if (email.isEmpty()) {
            throw new IllegalArgumentException(
                    "Registered login email is required");
        }

        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Farmer not found with this email"));

        if (farmer.getPassword() == null ||
                !farmer.getPassword().equals(
                        request.getCurrentPassword())) {
            throw new IllegalArgumentException(
                    "Current password is incorrect");
        }

        String newPassword = request.getNewPassword();

        if (newPassword.length() < 8 ||
                newPassword.length() > 72) {
            throw new IllegalArgumentException(
                    "Password must contain 8 to 72 characters");
        }

        if (!newPassword.equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "New password and confirm password do not match");
        }

        if (newPassword.equals(request.getCurrentPassword())) {
            throw new IllegalArgumentException(
                    "New password must differ from current password");
        }

        farmer.setPassword(newPassword);
        farmerRepository.save(farmer);
    }

    public void deleteFarmer(Long id) {
        if (!farmerRepository.existsById(id)) {
            throw new RuntimeException("Farmer not found");
        }

        farmerRepository.deleteById(id);
    }
}
