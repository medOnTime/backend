package com.medOnTime.medicineService.repo;

import com.medOnTime.medicineService.dto.MedicineDTO;

import java.util.HashMap;
import java.util.List;

public interface MedicineRepository {

    List<MedicineDTO> getAllMedicines();

    String addMedicineToInventry(HashMap<String, String> addedMedicine);

}
