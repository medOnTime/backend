package com.medOnTime.pharmacyService.service;

import com.medOnTime.pharmacyService.dto.PharmacyDTO;
import com.medOnTime.pharmacyService.repo.PharmacyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public String addPharmacy(String name, String address, String contactNumber,
                              String certificateNumber, String certifiedDate,
                              MultipartFile certificateFile) {

        try {
            // Duplication check
            if (pharmacyRepository.existsByCertificateNumber(certificateNumber)) {
                return "A pharmacy with this certificate number already exists.";
            }

            if (certificateFile.isEmpty()) {
                return "Certificate file is required.";
            }

            // Upload to S3 and get file URL
            String fileUrl = s3FileService.uploadFile(certificateFile);

            // Prepare data
            HashMap<String, String> pharmacyData = new HashMap<>();
            pharmacyData.put("name", name);
            pharmacyData.put("address", address);
            pharmacyData.put("contactNumber", contactNumber);
            pharmacyData.put("certificateNumber", certificateNumber);
            pharmacyData.put("certifiedNumber", certifiedDate);
            pharmacyData.put("filePath", fileUrl);

            // Save to DB
            return pharmacyRepository.addPharmacy(pharmacyData);

        } catch (IOException e) {
            return "Failed to upload certificate.";
        }
    }

    @Override
    public List<PharmacyDTO> getAllPharmacies(){
        return pharmacyRepository.getAllPharmacies();
    }

    @Override
    public List<HashMap<String, String>> getPharmacyById(String id) {
        return pharmacyRepository.getPharmacyById(Integer.parseInt(id));
    }

}
