package com.medOnTime.medicineService.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class MedicineDTO {

    @Id
    private int medicine_id;
    private String medicine_name;
    private String description;
    private String type;
    private  String strength;

}
