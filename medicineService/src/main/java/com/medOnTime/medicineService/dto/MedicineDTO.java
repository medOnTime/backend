package com.medOnTime.medicineService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicineDTO {

    private int medicine_id;
    private String medicine_name;
    private String description;
    private String type;
    private  String strength;

}
