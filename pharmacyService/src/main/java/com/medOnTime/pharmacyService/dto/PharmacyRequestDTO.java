package com.medOnTime.pharmacyService.dto;

import lombok.Data;

@Data
public class PharmacyRequestDTO {
    private String pharmacyName;
    private String address;
    private String contactNumber;
    private String licenseNumber;
    private String email;
    private String license;
    // Getters & setters
}