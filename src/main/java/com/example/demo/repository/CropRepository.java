package com.example.demo.repository;

import com.example.demo.entity.Crop;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Integer> {

    List<Crop> findByFarmerId(Long farmerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Crop c WHERE c.id = :id")
    Optional<Crop> findByIdForUpdate(@Param("id") Integer id);
}