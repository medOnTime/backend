package com.medOnTime.pharmacyService.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "pharmacies")
public class PharmacyDTO {

    @Id
    private int id;
    private String name;
    private String address;
    private String contactNumber;
    private String certificateNumber;
    private String certifiedNumber;
    private String filePath;
}
