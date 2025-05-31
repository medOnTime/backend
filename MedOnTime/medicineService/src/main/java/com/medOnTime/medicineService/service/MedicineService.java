package com.medOnTime.medicineService.service;

import com.medOnTime.medicineService.dto.MedicineDTO;

import java.util.HashMap;
import java.util.List;

public interface MedicineService {

    List<MedicineDTO> getAllMedicines();

    String addMedicineToInventry(HashMap<String,String> addedMedicine);

}
