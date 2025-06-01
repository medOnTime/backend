package com.medOnTime.medicineService.service;

import com.medOnTime.medicineService.dto.MedicineDTO;
import com.medOnTime.medicineService.repo.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class MedicineServiceimpl implements MedicineService{

    @Autowired
    private MedicineRepository medicineRepository;

    @Override
    public MedicineDTO getMedicineDetailById(String medicineId) {
        return medicineRepository.getMedicineDetailById(Integer.parseInt(medicineId));
    }

    @Override
    public List<MedicineDTO> getAllMedicines(){
        return medicineRepository.getAllMedicines();
    }

    @Override
    public String addMedicineToInventry(HashMap<String, String> addedMedicine) throws Exception {

        HashMap<String,String> medInventory = medicineRepository.getMedicineInventoryByUserIdAndMedicineID(Integer.parseInt(addedMedicine.get("userId")), Integer.parseInt(addedMedicine.get("medicineId")));

        if (!medInventory.isEmpty()){
            Integer newQuantity = Integer.parseInt(medInventory.get("quantity")) + Integer.parseInt(addedMedicine.get("quantity"));
            HashMap<String,String> updatedInventoryRec = new HashMap<>();
            updatedInventoryRec.put("quantity", newQuantity.toString());
            updatedInventoryRec.put("startDate", addedMedicine.get("startDate"));
            updatedInventoryRec.put("endDate", addedMedicine.get("endDate"));
            updatedInventoryRec.put("inventoryId", medInventory.get("inventoryId"));

            return medicineRepository.updateMedicineInventory(updatedInventoryRec);
        }

        return medicineRepository.addMedicineToInventry(addedMedicine);
    }

    @Override
    public List<HashMap<String, String>> getMedicineInventoryByUser(String userId) {
        return medicineRepository.getMedicineInventoryByUser(Integer.parseInt(userId));
    }

    @Override
    public String updateInventory(HashMap<String,String> updatedData){
        return medicineRepository.updateMedicineInventory(updatedData);
    }
}
