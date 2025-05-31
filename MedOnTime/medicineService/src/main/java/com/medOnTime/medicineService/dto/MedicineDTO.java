package com.medOnTime.medicineService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineDTO {

    private int medicine_id;
    private String medicine_name;
    private String description;

}
