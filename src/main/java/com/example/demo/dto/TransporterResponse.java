package com.example.demo.dto;

public record TransporterResponse(
    Long id,
    String fullName,
    String phone,
    String email,
    String profilePhoto,
    String businessName,
    String transporterType,
    String village,
    String district,
    String state,
    String vehicleNumber,
    String vehicleType,
    Double vehicleCapacity,
    String capacityUnit,
    Double latitude,
    Double longitude,
    String currentlyAvailable
) {}