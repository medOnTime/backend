package com.medOnTime.pharmacyService.repo;

import com.medOnTime.pharmacyService.dto.PharmacyDTO;
import com.medOnTime.pharmacyService.dto.PharmacySelectionDTO;

import java.util.HashMap;
import java.util.List;

public interface PharmacyRepository {
    String addPharmacy(HashMap<String, String> addedPharmacyDetails);

    boolean existsByLicenseNumber(String licenseNumber);

    List<PharmacyDTO> getAllPharmacies();

    List<PharmacySelectionDTO> getAllPharmaciesForSelection();

    String setApproval(int pharmacyId);

    String checkStatus(int pharmacyId);

    void updateSecretKey(int pharmacyId, String encodedKey);
}
