package com.medOnTime.pharmacyService.service;

import com.medOnTime.pharmacyService.repo.PharmacyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class PharmacyServiceImpl implements PharmacyService {


    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private S3FileService s3FileService;

    @Override
    @Transactional
    public String addPharmacy(String pharmacyName, String address, String contactNumber, String licenseNumber,  MultipartFile licence) {

        try {
            System.out.println(licenseNumber);
            if (pharmacyRepository.existsByLicenseNumber(licenseNumber)) {
                return "A pharmacy with this certificate number already exists.";
            }

            if (licence.isEmpty()) {
                return "license file is required.";
            }

            // Upload to S3 and get file URL
            String fileUrl = s3FileService.uploadFile(licence);

            // Prepare data
            HashMap<String, String> pharmacyData = new HashMap<>();
            pharmacyData.put("pharmacyName", pharmacyName);
            pharmacyData.put("address", address);
            pharmacyData.put("contactNumber", contactNumber);
            pharmacyData.put("licenseNumber", licenseNumber);
            pharmacyData.put("license", fileUrl);

            // Save to DB
            return pharmacyRepository.addPharmacy(pharmacyData);

        } catch (IOException e) {
            return "Failed to upload certificate.";
        }
    }


}
