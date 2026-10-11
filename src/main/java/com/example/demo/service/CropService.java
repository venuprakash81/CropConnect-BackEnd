package com.example.demo.service;

import com.example.demo.entity.Crop;
import com.example.demo.entity.Farmer;
import com.example.demo.repository.CropRepository;
import com.example.demo.repository.FarmerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CropService {

    private final CropRepository cropRepository;
    private final FarmerRepository farmerRepository;

    public CropService(
            CropRepository cropRepository,
            FarmerRepository farmerRepository) {
        this.cropRepository = cropRepository;
        this.farmerRepository = farmerRepository;
    }

    @Transactional(readOnly = true)
    public List<Crop> getAllCrops() {
        List<Crop> crops = cropRepository.findAll();

        for (Crop crop : crops) {
            if (crop.getFarmerId() != null) {
                farmerRepository.findById(crop.getFarmerId())
                        .ifPresent(farmer ->
                                crop.setFarmerEmail(farmer.getEmail()));
            }
        }

        return crops;
    }

    @Transactional(readOnly = true)
    public List<Crop> getCropsByFarmer(Long farmerId) {
        return cropRepository.findByFarmerId(farmerId);
    }

    @Transactional(readOnly = true)
    public List<Crop> getCropsByFarmerEmail(String email) {
        Farmer farmer = farmerRepository.findByEmail(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException("Farmer not found"));

        return cropRepository.findByFarmerId(farmer.getId());
    }

    @Transactional(readOnly = true)
    public Optional<Crop> getCropById(Integer id) {
        return cropRepository.findById(id);
    }

    public Crop saveCrop(Crop crop) {
        return cropRepository.save(crop);
    }

    public void deleteCrop(Integer id) {
        cropRepository.deleteById(id);
    }
}