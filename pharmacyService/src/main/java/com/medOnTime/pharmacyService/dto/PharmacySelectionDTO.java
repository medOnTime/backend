package com.medOnTime.pharmacyService.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PharmacySelectionDTO {
    int pharmacyId;
    String pharmacyName;
    String address;
}
