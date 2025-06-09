package com.medOnTime.medicineService.repo;

import com.medOnTime.medicineService.dto.MedicineDTO;

import java.util.HashMap;
import java.util.List;

public interface MedicineRepository {

    MedicineDTO getMedicineDetailById(int medicineId);

    List<MedicineDTO> getAllMedicines();

    String addMedicineToInventry(HashMap<String, String> addedMedicine) throws Exception;

    List<HashMap<String, String>> getMedicineInventoryByUser(int userId);

    HashMap<String, String> getMedicineInventoryByUserIdAndMedicineID(int userId, int medicineId);

    String updateMedicineInventory(HashMap<String, String> updatedMedicineInventory);

    Integer getQuantityOfMedicineFromInventoryBuyUserAndMedId(Integer userId, Integer medicineId);

    String updateQuantityOfInventryByUserAndMedId(Integer userId, Integer medicineId, Integer newQuantity);

}
