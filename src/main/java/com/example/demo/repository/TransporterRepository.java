
package com.example.demo.repository;

import com.example.demo.entity.Transporter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransporterRepository
        extends JpaRepository<Transporter, Long> {

    Optional<Transporter> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByVehicleNumber(String vehicleNumber);

    List<Transporter> findAllByOrderByIdDesc();
}
