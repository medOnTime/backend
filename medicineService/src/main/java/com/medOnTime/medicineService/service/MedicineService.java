package com.medOnTime.medicineService.service;

import com.medOnTime.medicineService.dto.MedicineDTO;

import java.util.HashMap;
import java.util.List;

public interface MedicineService {

    MedicineDTO getMedicineDetailById(String medicineId);

    List<MedicineDTO> getAllMedicines();

    String addMedicineToInventry(HashMap<String,String> addedMedicine) throws Exception;

    List<HashMap<String,String>> getMedicineInventoryByUser(String userId);

    String updateInventory(HashMap<String,String> updatedData);

}
