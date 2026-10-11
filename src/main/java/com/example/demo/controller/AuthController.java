package com.example.demo.controller;

import com.example.demo.entity.Farmer;
import com.example.demo.entity.Buyer;
import com.example.demo.entity.Transporter;
import com.example.demo.entity.Warehouse;

import com.example.demo.repository.FarmerRepository;
import com.example.demo.repository.BuyerRepository;
import com.example.demo.repository.TransporterRepository;
import com.example.demo.repository.WarehouseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {


    @Autowired
    private FarmerRepository farmerRepository;


    @Autowired
    private BuyerRepository buyerRepository;


    @Autowired
    private TransporterRepository transporterRepository;


    @Autowired
    private WarehouseRepository warehouseRepository;


    // =====================================================
    // FARMER LOGIN
    // =====================================================

    @PostMapping("/farmer-login")
    public ResponseEntity<?> farmerLogin(
            @RequestBody LoginRequest request
    ) {

        return farmerRepository
                .findByEmail(request.getEmail())
                .map(farmer -> {

                    if (!farmer.getPassword()
                            .equals(request.getPassword())) {

                        return ResponseEntity
                                .status(401)
                                .body(
                                    Map.of(
                                        "message",
                                        "Invalid farmer email or password."
                                    )
                                );
                    }


                    Map<String, Object> result =
                            new HashMap<>();


                    result.put(
                            "message",
                            "Farmer login successful"
                    );

                    result.put(
                            "userType",
                            "Farmer"
                    );

                    result.put(
                            "userId",
                            farmer.getId()
                    );

                    result.put(
                            "name",
                            farmer.getFullName()
                    );

                    result.put(
                            "email",
                            farmer.getEmail()
                    );


                    return ResponseEntity.ok(result);

                })
                .orElseGet(() ->
                        ResponseEntity
                                .status(401)
                                .body(
                                    Map.of(
                                        "message",
                                        "Farmer account not found."
                                    )
                                )
                );
    }



    // =====================================================
    // BUYER LOGIN
    // =====================================================

    @PostMapping("/buyer-login")
    public ResponseEntity<?> buyerLogin(
            @RequestBody LoginRequest request
    ) {

        return buyerRepository
                .findByEmailIgnoreCase(request.getEmail())
                .map(buyer -> {

                    if (!buyer.getPassword()
                            .equals(request.getPassword())) {

                        return ResponseEntity
                                .status(401)
                                .body(
                                    Map.of(
                                        "message",
                                        "Invalid buyer email or password."
                                    )
                                );
                    }


                    Map<String, Object> result =
                            new HashMap<>();


                    result.put(
                            "message",
                            "Buyer login successful"
                    );

                    result.put(
                            "userType",
                            "Buyer"
                    );

                    result.put(
                            "userId",
                            buyer.getId()
                    );

                    result.put(
                            "name",
                            buyer.getFullName()
                    );

                    result.put(
                            "email",
                            buyer.getEmail()
                    );


                    return ResponseEntity.ok(result);

                })
                .orElseGet(() ->
                        ResponseEntity
                                .status(401)
                                .body(
                                    Map.of(
                                        "message",
                                        "Buyer account not found."
                                    )
                                )
                );
    }



    // =====================================================
    // TRANSPORTER LOGIN
    // =====================================================

    @PostMapping("/transporter-login")
    public ResponseEntity<?> transporterLogin(
            @RequestBody LoginRequest request
    ) {

        return transporterRepository
                .findByEmail(request.getEmail())
                .map(transporter -> {

                    if (!transporter.getPassword()
                            .equals(request.getPassword())) {

                        return ResponseEntity
                                .status(401)
                                .body(
                                    Map.of(
                                        "message",
                                        "Invalid transporter email or password."
                                    )
                                );
                    }


                    Map<String, Object> result =
                            new HashMap<>();


                    result.put(
                            "message",
                            "Transporter login successful"
                    );

                    result.put(
                            "userType",
                            "Transporter"
                    );

                    result.put(
                            "userId",
                            transporter.getId()
                    );

                    result.put(
                            "name",
                            transporter.getFullName()
                    );

                    result.put(
                            "email",
                            transporter.getEmail()
                    );


                    return ResponseEntity.ok(result);

                })
                .orElseGet(() ->
                        ResponseEntity
                                .status(401)
                                .body(
                                    Map.of(
                                        "message",
                                        "Transporter account not found."
                                    )
                                )
                );
    }



    // =====================================================
    // WAREHOUSE LOGIN
    // =====================================================

    @PostMapping("/warehouse-login")
    public ResponseEntity<?> warehouseLogin(
            @RequestBody LoginRequest request
    ) {

        return warehouseRepository
                .findByEmail(request.getEmail())
                .map(warehouse -> {

                    if (!warehouse.getPassword()
                            .equals(request.getPassword())) {

                        return ResponseEntity
                                .status(401)
                                .body(
                                    Map.of(
                                        "message",
                                        "Invalid warehouse email or password."
                                    )
                                );
                    }


                    Map<String, Object> result =
                            new HashMap<>();


                    result.put(
                            "message",
                            "Warehouse login successful"
                    );

                    result.put(
                            "userType",
                            "Warehouse"
                    );

                    result.put(
                            "userId",
                            warehouse.getId()
                    );

                    result.put(
                            "name",
                            warehouse.getOwnerName()
                    );

                    result.put(
                            "email",
                            warehouse.getEmail()
                    );


                    return ResponseEntity.ok(result);

                })
                .orElseGet(() ->
                        ResponseEntity
                                .status(401)
                                .body(
                                    Map.of(
                                        "message",
                                        "Warehouse account not found."
                                    )
                                )
                );
    }



    // =====================================================
    // LOGIN REQUEST
    // =====================================================

    public static class LoginRequest {

        private String email;

        private String password;


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

    }

}