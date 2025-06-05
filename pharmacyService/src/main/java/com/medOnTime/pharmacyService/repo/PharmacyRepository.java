package com.medOnTime.pharmacyService.repo;

import com.medOnTime.pharmacyService.dto.PharmacyDTO;

import java.util.HashMap;
import java.util.List;

public interface PharmacyRepository {
    String addPharmacy(HashMap<String, String> addedPharmacyDetails);

    boolean existsByCertificateNumber(String certificateNumber);

    List<PharmacyDTO> getAllPharmacies();
}
