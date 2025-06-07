package com.medOnTime.pharmacyService.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class PharmacyRepositoryImpl implements PharmacyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public boolean existsByLicenseNumber(String licenseNumber) {
        Query checkQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM pharmacy WHERE license_number = :licenseNumber");
        checkQuery.setParameter("licenseNumber", licenseNumber);
        Number count = (Number) checkQuery.getSingleResult();
        return count.intValue() > 0;
    }


    @Override
    @Transactional
    public String addPharmacy(HashMap<String, String> addedPharmacyDetails) {
        Query insertQuery = entityManager.createNativeQuery(
                "INSERT INTO pharmacy (pharmacy_name, address, contact_number, license_number, license) " +
                        "VALUES (:pharmacyName, :address, :contactNumber,:licenseNumber, :license)"
        );


        insertQuery.setParameter("pharmacyName", addedPharmacyDetails.get("pharmacyName"));
        insertQuery.setParameter("address", addedPharmacyDetails.get("address"));
        insertQuery.setParameter("contactNumber", addedPharmacyDetails.get("contactNumber"));
        insertQuery.setParameter("licenseNumber", addedPharmacyDetails.get("licenseNumber"));
        insertQuery.setParameter("license", addedPharmacyDetails.get("license"));


        int rows = insertQuery.executeUpdate();
        return rows > 0 ? "Pharmacy added successfully" : "Insertion failed";
    }
}
