package com.medOnTime.pharmacyService.service;

import com.medOnTime.pharmacyService.dto.EmailRequestDTO;
import com.medOnTime.pharmacyService.dto.PharmacyDTO;
import com.medOnTime.pharmacyService.dto.PharmacySelectionDTO;
import com.medOnTime.pharmacyService.repo.PharmacyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PharmacyServiceImpl implements PharmacyService {


    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private S3FileService s3FileService;

    @Autowired
    private PasswordEncoder passwordEncoder;    // BCrypt, Argon2, PBKDF2, …

    @Autowired
    private WebClient emailWebClient;


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
        if(pharmacyRepository.checkStatus(pharmacyId).equals("PENDING")){
            pharmacyRepository.setApproval(pharmacyId);
            String rawKey = generateSecretKey();
            System.out.println(rawKey);
            String encodedKey = passwordEncoder.encode(rawKey);

            pharmacyRepository.updateSecretKey(pharmacyId, encodedKey); // implement this in repo
            String email = pharmacyRepository.findEmail(pharmacyId);  // implement in repo
            String name = pharmacyRepository.findName(pharmacyId);

            EmailRequestDTO request = new EmailRequestDTO();
            request.setTo(email);
            request.setSubject("Your pharmacy has been approved!");
            request.setName(name);
            request.setSecretKey(rawKey); // new field you added to template

            try {
                emailWebClient.post()
                        .uri("/email/send")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> System.err.println("Email service error: " + e.getMessage()))
                        .block(); // Use subscribe() if you want async
            } catch (Exception e) {
                throw new RuntimeException("Email sending failed", e);
            }

            return "Pharmacy approved successfully";
        }else {
            return "Pharmacy already approved";
        }

    }

    @Transactional
    @Override
    public String setRejection(int pharmacyId) {
        if(pharmacyRepository.checkStatus(pharmacyId).equals("PENDING") && !(pharmacyRepository.checkStatus(pharmacyId).equals("APPROVED"))){
            pharmacyRepository.setRejection(pharmacyId);

            String email = pharmacyRepository.findEmail(pharmacyId);  // implement in repo
            String name = pharmacyRepository.findName(pharmacyId);

            EmailRequestDTO request = new EmailRequestDTO();
            request.setTo(email);
            request.setSubject("Your pharmacy has been rejected!");
            request.setName(name);

            try {
                emailWebClient.post()
                        .uri("/email/send")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> System.err.println("Email service error: " + e.getMessage()))
                        .block(); // Use subscribe() if you want async
            } catch (Exception e) {
                throw new RuntimeException("Email sending failed", e);
            }
            return "Pharmacy rejected successfully";
        }else{
            return "Pharmacy rejection failed";
        }
    }

    @Override
    public String getLicensePresignedUrl(String licenseNumber) {
        String s3Key = pharmacyRepository.findLicenseFileKeyByLicenseNumber(licenseNumber);

        if (s3Key == null || s3Key.isEmpty()) {
            throw new NoSuchElementException("License file not found.");
        }

        return s3FileService.generatePresignedUrl(s3Key);
    }

    @Override
    public String getPharmacySecretById(int pharmacyId) {
        return pharmacyRepository.getPharmacyKeyById(pharmacyId);

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
