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

    @GetMapping("/getAllMedicines")
    public List<MedicineDTO> getAllMedicines(){
        return medicineService.getAllMedicines();
    }

    @PostMapping("addMedicineToInventry")
    public String addMedicineToInventry(@RequestBody HashMap<String,String> addedMedicineDetails){
        return null;
    }
}
