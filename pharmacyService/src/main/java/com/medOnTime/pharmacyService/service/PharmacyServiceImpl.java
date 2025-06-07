package com.medOnTime.pharmacyService.service;

import com.medOnTime.pharmacyService.dto.PharmacyDTO;
import com.medOnTime.pharmacyService.dto.PharmacySelectionDTO;
import com.medOnTime.pharmacyService.repo.PharmacyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class PharmacyServiceImpl implements PharmacyService {


    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private S3FileService s3FileService;

    @Autowired
    private PasswordEncoder passwordEncoder;    // BCrypt, Argon2, PBKDF2, …


    @Override
    @Transactional
    public String addPharmacy(String pharmacyName, String address, String contactNumber, String licenseNumber, String email,  MultipartFile licence) {

        try {
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
            pharmacyData.put("email", email);
            pharmacyData.put("license", fileUrl);

            // Save to DB
            return pharmacyRepository.addPharmacy(pharmacyData);

        } catch (IOException e) {
            return "Failed to upload certificate.";
        }
    }

    @Override
    public List<PharmacyDTO> getAllPharmacies() {
        return pharmacyRepository.getAllPharmacies();
    }

    @Override
    public List<PharmacySelectionDTO> getAllPharmaciesForSelection() {
        return pharmacyRepository.getAllPharmaciesForSelection();
    }

    @Transactional
    @Override
    public String setApproval(int pharmacyId) {
        if(pharmacyRepository.checkStatus(pharmacyId).equals("FALSE")){
            pharmacyRepository.setApproval(pharmacyId);
            String rawKey = generateSecretKey();
            System.out.println(rawKey);
            String encodedKey = passwordEncoder.encode(rawKey);

            pharmacyRepository.updateSecretKey(pharmacyId, encodedKey); // implement this in repo

            //Todo --> String email = pharmacyRepository.findEmail(pharmacyId); // implement this in repo
            //Todo --> emailService.sendApprovalEmail(email, rawKey);           // implement this method

            return "Pharmacy approved successfully";
        }else {
            return "Pharmacy already approved";
        }

    }

    private String generateSecretKey() {
        try {
            byte[] bytes = new byte[32];
            SecureRandom random = SecureRandom.getInstanceStrong();
            random.nextBytes(bytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SecureRandom algorithm not available", e);
        }
    }

}
