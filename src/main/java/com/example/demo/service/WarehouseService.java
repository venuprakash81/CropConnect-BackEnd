package com.example.demo.service;

import com.example.demo.entity.Warehouse;
import com.example.demo.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public Warehouse registerWarehouse(Warehouse warehouse) {
        if (warehouse.getEmail() == null
                || warehouse.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (warehouse.getPassword() == null
                || warehouse.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (warehouseRepository.existsByEmail(warehouse.getEmail())) {
            throw new IllegalArgumentException(
                    "Warehouse email already exists");
        }

        return warehouseRepository.save(warehouse);
    }

    public Optional<Warehouse> login(String email, String password) {
        return warehouseRepository.findByEmail(email)
                .filter(warehouse ->
                        warehouse.getPassword() != null
                        && warehouse.getPassword().equals(password));
    }

    public Optional<Warehouse> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }

    public Optional<Warehouse> getWarehouseByEmail(String email) {
        return warehouseRepository.findByEmail(email);
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Transactional
    public Warehouse updateWarehouse(Long id, Warehouse updatedWarehouse) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Warehouse not found"));

        warehouse.setWarehouseName(updatedWarehouse.getWarehouseName());
        warehouse.setOwnerName(updatedWarehouse.getOwnerName());
        warehouse.setPhone(updatedWarehouse.getPhone());
        warehouse.setAddress(updatedWarehouse.getAddress());
        warehouse.setCity(updatedWarehouse.getCity());
        warehouse.setState(updatedWarehouse.getState());
        warehouse.setLatitude(updatedWarehouse.getLatitude());
        warehouse.setLongitude(updatedWarehouse.getLongitude());
        warehouse.setTotalStorageCapacity(
                updatedWarehouse.getTotalStorageCapacity());
        warehouse.setSupportedCrops(updatedWarehouse.getSupportedCrops());
        warehouse.setProfilePhoto(updatedWarehouse.getProfilePhoto());
        warehouse.setWarehouseDocument(
                updatedWarehouse.getWarehouseDocument());
        warehouse.setColdStorage(updatedWarehouse.isColdStorage());
        warehouse.setLoadingFacility(updatedWarehouse.isLoadingFacility());
        warehouse.setSecurityAvailable(
                updatedWarehouse.isSecurityAvailable());

        return warehouseRepository.save(warehouse);
    }

    @Transactional
    public boolean changePassword(
            Long id, String currentPassword, String newPassword) {

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Warehouse not found"));

        if (currentPassword == null
                || !currentPassword.equals(warehouse.getPassword())) {
            return false;
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "New password is required");
        }

        warehouse.setPassword(newPassword);
        warehouseRepository.save(warehouse);
        return true;
    }

    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new IllegalArgumentException("Warehouse not found");
        }

        warehouseRepository.deleteById(id);
    }
}