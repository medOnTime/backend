package com.medOnTime.pharmacyService.controller;

import com.medOnTime.pharmacyService.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@RestController
@RequestMapping("pharmacy")
public class PharmacyController {

    @Autowired
    private PharmacyService pharmacyService;

    @PostMapping("/add-pharmacy")
    public String addPharmacy(
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("certificateNumber") String certificateNumber,
            @RequestParam("certifiedDate") String certifiedDate,
            @RequestParam("certificateFile") MultipartFile certificateFile
    ) throws Exception {
        return pharmacyService.addPharmacy(name, address, contactNumber,
                certificateNumber, certifiedDate, certificateFile);
    }

    @GetMapping("/getAllPharmacies")
    public

}
