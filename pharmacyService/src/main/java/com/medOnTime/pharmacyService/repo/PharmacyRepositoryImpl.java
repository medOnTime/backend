package com.medOnTime.pharmacyService.repo;

import com.medOnTime.pharmacyService.dto.PharmacyDTO;
import com.medOnTime.pharmacyService.dto.PharmacySelectionDTO;
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
    public List<PharmacyDTO> getAllPharmacies() {
        Query query = entityManager.createNativeQuery("select pharmacy_id,pharmacy_name,address,contact_number,license_number,status,email,license from pharmacy order by pharmacy_id DESC", PharmacyDTO.class);

        List<PharmacyDTO> pharmacyDTOS = query.getResultList();

        return pharmacyDTOS;    }

    @Override
    public List<PharmacySelectionDTO> getAllPharmaciesForSelection() {
        Query query = entityManager.createNativeQuery("select pharmacy_id,pharmacy_name,address from pharmacy where status = 'APPROVED'", PharmacySelectionDTO.class);
        List<PharmacySelectionDTO> pharmacySelectionDTOS = query.getResultList();
        return pharmacySelectionDTOS;
    }

    @Override
    public String setApproval(int pharmacyId) {

            Query updateQuery = entityManager.createNativeQuery("UPDATE pharmacy SET status = 'APPROVED' WHERE pharmacy_id = :pharmacyId");
            updateQuery.setParameter("pharmacyId", pharmacyId);
            int rows = updateQuery.executeUpdate();


        return rows > 0 ? "Pharmacy approved successfully" : "Approval failed";
    }

    @Override
    public String checkStatus(int pharmacyId) {
        Query checkQuery = entityManager.createNativeQuery("SELECT status FROM pharmacy WHERE pharmacy_id = :pharmacyId");
        checkQuery.setParameter("pharmacyId", pharmacyId);
        String status = (String) checkQuery.getSingleResult();
        return status;
    }

    @Override
    public void updateSecretKey(int pharmacyId, String encodedKey) {
        Query query = entityManager.createNativeQuery("UPDATE pharmacy SET secret_key = :encodedKey WHERE pharmacy_id = :pharmacyId");
        query.setParameter("encodedKey", encodedKey);
        query.setParameter("pharmacyId", pharmacyId);
        query.executeUpdate();
    }

    @Override
    public String findEmail(int pharmacyId) {
        Query query = entityManager.createNativeQuery("SELECT email FROM pharmacy WHERE pharmacy_id = :pharmacyId");
        query.setParameter("pharmacyId", pharmacyId);
        String email = (String) query.getSingleResult();
        return email;
    }

    @Override
    public String findName(int pharmacyId) {
        Query query = entityManager.createNativeQuery("SELECT pharmacy_name FROM pharmacy WHERE pharmacy_id = :pharmacyId");
        query.setParameter("pharmacyId", pharmacyId);
        String name = (String) query.getSingleResult();
        return name;
    }

    @Override
    public void setRejection(int pharmacyId) {
        Query query = entityManager.createNativeQuery("UPDATE pharmacy SET status = 'REJECTED' WHERE pharmacy_id = :pharmacyId");
        query.setParameter("pharmacyId", pharmacyId);
        query.executeUpdate();
    }

    @Override
    public String findLicenseFileKeyByLicenseNumber(String licenseNumber) {
        Query query = entityManager.createNativeQuery("SELECT license FROM pharmacy WHERE license_number = :licenseNumber");
        query.setParameter("licenseNumber", licenseNumber);
        String license = (String) query.getSingleResult();
        System.out.println(license);
        return license;
    }


    @Override
    @Transactional
    public String addPharmacy(HashMap<String, String> addedPharmacyDetails) {
        Query insertQuery = entityManager.createNativeQuery(
                "INSERT INTO pharmacy (pharmacy_name, address, contact_number, license_number,email,status, license) " +
                        "VALUES (:pharmacyName, :address, :contactNumber,:licenseNumber,:email, 'PENDING', :license)"
        );


        insertQuery.setParameter("pharmacyName", addedPharmacyDetails.get("pharmacyName"));
        insertQuery.setParameter("address", addedPharmacyDetails.get("address"));
        insertQuery.setParameter("contactNumber", addedPharmacyDetails.get("contactNumber"));
        insertQuery.setParameter("licenseNumber", addedPharmacyDetails.get("licenseNumber"));
        insertQuery.setParameter("email", addedPharmacyDetails.get("email"));
        insertQuery.setParameter("license", addedPharmacyDetails.get("license"));


        int rows = insertQuery.executeUpdate();
        return rows > 0 ? "Pharmacy added successfully" : "Insertion failed";
    }
}
