package com.medOnTime.pharmacyService.repo;

import java.util.HashMap;

public interface PharmacyRepository {
    String addPharmacy(HashMap<String, String> addedPharmacyDetails);

    boolean existsByCertificateNumber(String certificateNumber);
}
