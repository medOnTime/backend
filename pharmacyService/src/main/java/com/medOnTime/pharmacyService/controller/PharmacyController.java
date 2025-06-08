package com.medOnTime.pharmacyService.controller;


import com.medOnTime.pharmacyService.dto.PharmacyDTO;
import com.medOnTime.pharmacyService.dto.PharmacyRequestDTO;
import com.medOnTime.pharmacyService.dto.PharmacySelectionDTO;
import com.medOnTime.pharmacyService.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

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
                data.getPharmacyName(), data.getAddress(), data.getContactNumber(), data.getLicenseNumber(), data.getEmail(), license
        );
    }

    @GetMapping("/get-all-pharmacies")
    public List<PharmacyDTO> getAllPharmacies() {
        return pharmacyService.getAllPharmacies();
    }

    @GetMapping("/get-all-pharmacies-for-selection")
    public List<PharmacySelectionDTO> getAllPharmaciesForSelection() {
        return pharmacyService.getAllPharmaciesForSelection();
    }

    @PostMapping("/set-approval")
    public String setPharmacyApproval(@RequestParam(value = "pharmacyId") int pharmacyId) {
        return pharmacyService.setApproval(pharmacyId);
    }

    @PostMapping("/set-rejection")
    public String setPharmacyRejection(@RequestParam(value = "pharmacyId") int pharmacyId) {
        return pharmacyService.setRejection(pharmacyId);
    }

    @GetMapping("/certificate-url/{licenseNumber}")
    public ResponseEntity<String> getCertificateUrl(@PathVariable String licenseNumber) {
        try {
            String presignedUrl = pharmacyService.getLicensePresignedUrl(licenseNumber);
            return ResponseEntity.ok(presignedUrl);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
