package com.medOnTime.medicineService.controller;

import com.medOnTime.medicineService.dto.MedicineDTO;
import com.medOnTime.medicineService.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("medicine_service")
public class medicineController {

    @Autowired
    private MedicineService medicineService;

    @GetMapping("/getMedicineById")
    public MedicineDTO getMedicineById(@RequestParam(value = "medicineId") String medicineId){
        return medicineService.getMedicineDetailById(medicineId);
    }

    @GetMapping("/getAllMedicines")
    public List<MedicineDTO> getAllMedicines(){
        return medicineService.getAllMedicines();
    }

    @PostMapping("addMedicineToInventry")
    public String addMedicineToInventry(@RequestBody HashMap<String,String> addedMedicineDetails) throws Exception {
        return medicineService.addMedicineToInventry(addedMedicineDetails);
    }

    @PostMapping("/getMedicineInventoryByUser")
    public List<HashMap<String,String>> getMedicineInventoryByUser(@RequestBody String userId){
        return medicineService.getMedicineInventoryByUser(userId);
    }

    @PostMapping("/updateInventory")
    public String updateInventory(@RequestBody HashMap<String,String> updatedData){
        return medicineService.updateInventory(updatedData);
    }
}
