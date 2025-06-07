package com.medOnTime.pharmacyService.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class PharmacyDTO {

    @Id
    private int pharmacyId;
    private String pharmacyName;
    private String address;
    private String contactNumber;
    private String licenseNumber;
    private String email;
    private String status;
    private String license;


}
