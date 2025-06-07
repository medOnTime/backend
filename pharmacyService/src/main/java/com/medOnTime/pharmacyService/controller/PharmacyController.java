package com.medOnTime.pharmacyService.controller;


import com.medOnTime.pharmacyService.dto.PharmacyRequestDTO;
import com.medOnTime.pharmacyService.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("pharmacy")
public class PharmacyController {

    @Autowired
    private PharmacyService pharmacyService;

    @PostMapping(value = "/add-pharmacy", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addPharmacy(
            @RequestPart("data") PharmacyRequestDTO data,
            @RequestPart("license") MultipartFile license
    ) throws Exception {
        return pharmacyService.addPharmacy(
                data.getPharmacyName(), data.getAddress(), data.getContactNumber(), data.getLicenseNumber(), license
        );
    }



}
