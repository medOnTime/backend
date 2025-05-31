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
    public List<MedicineDTO> getAllMedicines(){
        return medicineRepository.getAllMedicines();
    }

    @Override
    public String addMedicineToInventry(HashMap<String, String> addedMedicine) {
        return null;
    }
}
