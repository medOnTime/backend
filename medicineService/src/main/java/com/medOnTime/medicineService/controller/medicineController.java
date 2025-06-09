package com.medOnTime.medicineService.controller;

import com.medOnTime.medicineService.dto.MedicineDTO;
import com.medOnTime.medicineService.dto.ReminderSchedulesDTO;
import com.medOnTime.medicineService.service.MedicineService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("medicine")
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

    @GetMapping("/getMedicineInventoryByUser")
    public List<HashMap<String,String>> getMedicineInventoryByUser(HttpServletRequest request){
        String userId = request.getHeader("X-User-Id");
        return medicineService.getMedicineInventoryByUser(userId);
    }

    @PostMapping("/updateInventory")
    public String updateInventory(@RequestBody HashMap<String,String> updatedData){
        return medicineService.updateInventory(updatedData);
    }

    @PostMapping("/updateInventoryAfterScheduleAction")
    public String updateInventoryAfterScheduleAction(@RequestBody ReminderSchedulesDTO schedulesDTO){
        return medicineService.updateInventoryAfterScheduleAction(schedulesDTO);
    }

}
