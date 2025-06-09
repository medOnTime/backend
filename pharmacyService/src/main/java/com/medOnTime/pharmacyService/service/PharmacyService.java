package com.medOnTime.pharmacyService.service;


import com.medOnTime.pharmacyService.dto.PharmacyDTO;
import com.medOnTime.pharmacyService.dto.PharmacySelectionDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface PharmacyService {

    String addPharmacy(String pharmacyName,String address, String contactNumber, String licenseNumber, String email, MultipartFile licence) throws IOException;

    List<PharmacyDTO> getAllPharmacies();

    List<PharmacySelectionDTO> getAllPharmaciesForSelection();

    String setApproval(int pharmacyId);

    String setRejection(int pharmacyId);

    String getLicensePresignedUrl(String licenseNumber);

    String getPharmacySecretById(int pharmacyId);
}
