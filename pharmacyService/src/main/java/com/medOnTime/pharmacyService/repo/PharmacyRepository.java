package com.medOnTime.pharmacyService.repo;

import java.util.HashMap;
import java.util.List;

public interface PharmacyRepository {
    String addPharmacy(HashMap<String, String> addedPharmacyDetails);

    boolean existsByLicenseNumber(String licenseNumber);
}
