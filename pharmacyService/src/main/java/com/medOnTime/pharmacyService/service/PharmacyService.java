package com.medOnTime.pharmacyService.service;

import com.medOnTime.pharmacyService.dto.PharmacyDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PharmacyService {

    String addPharmacy(String name, String address, String contactNumber,
                               String certificateNumber, String certifiedDate,
                               MultipartFile certificateFile) throws IOException;

    List<PharmacyDTO> getAllPharmacies();
}
