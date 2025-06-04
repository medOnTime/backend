package com.medOnTime.pharmacyService.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class PharmacyRepositoryImpl implements PharmacyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean existsByCertificateNumber(String certificateNumber) {
        Query checkQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM pharmacies WHERE certification_code = :certificateNumber");
        checkQuery.setParameter("certificateNumber", certificateNumber);
        Number count = (Number) checkQuery.getSingleResult();
        return count.intValue() > 0;
    }

    @Override
    @Transactional
    public String addPharmacy(HashMap<String, String> addedPharmacyDetails) {
        Query insertQuery = entityManager.createNativeQuery(
                "INSERT INTO pharmacies (name, address, contact_number, certification_code, certified_date, file_path) " +
                        "VALUES (:name, :address, :contactNumber, :certificateNumber, :certifiedDate, :filePath)"
        );


        insertQuery.setParameter("name", addedPharmacyDetails.get("name"));
        insertQuery.setParameter("address", addedPharmacyDetails.get("address"));
        insertQuery.setParameter("contactNumber", addedPharmacyDetails.get("contactNumber"));
        insertQuery.setParameter("certificateNumber", addedPharmacyDetails.get("certificateNumber"));
        insertQuery.setParameter("certifiedDate", addedPharmacyDetails.get("certifiedDate"));
        insertQuery.setParameter("filePath", addedPharmacyDetails.get("filePath"));

        int rows = insertQuery.executeUpdate();
        return rows > 0 ? "Pharmacy added successfully" : "Insertion failed";
    }
}
