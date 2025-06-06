package com.medOnTime.pharmacyService.repo;

import com.medOnTime.pharmacyService.dto.PharmacyDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PharmacyRepositoryImpl implements PharmacyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean existsByCertificateNumber(String certificateNumber) {
        Query checkQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM pharmacies WHERE certification_code = :certificateNumber");
        checkQuery.setParameter("certificateNumber", certificateNumber);
        Number count = (Number) checkQuery.getSingleResult();
        return count.intValue() > 0;
    }



    public List<PharmacyDTO> getAllPharmacies() {
        String sql = "SELECT id, name, address, contact_number, certification_code, certified_date, file_path FROM pharmacies";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PharmacyDTO dto = new PharmacyDTO();
            dto.setId(rs.getInt("id"));
            dto.setName(rs.getString("name"));
            dto.setAddress(rs.getString("address"));
            dto.setContactNumber(rs.getString("contact_number"));
            dto.setCertificationCode(rs.getString("certification_code"));
            dto.setCertifiedDate(rs.getString("certified_date"));
            dto.setFilePath(rs.getString("file_path"));
            return dto;
        });
    }

    @Override
    public List<HashMap<String, String>> getPharmacyById(int id) {

        String sql = """
        SELECT id, name, address, contact_number, certification_code,
               certified_date, file_path
        FROM pharmacies
        WHERE id = ?
    """;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);

        List<HashMap<String, String>> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            HashMap<String, String> pharmacy = new HashMap<>();
            pharmacy.put("id", String.valueOf(row.get("id")));
            pharmacy.put("name", Objects.toString(row.get("name"), ""));
            pharmacy.put("address", Objects.toString(row.get("address"), ""));
            pharmacy.put("contactNumber", Objects.toString(row.get("contact_number"), ""));
            pharmacy.put("certificateNumber", Objects.toString(row.get("certification_code"), ""));
            pharmacy.put("certifiedDate", Objects.toString(row.get("certified_date"), ""));
            pharmacy.put("filePath", Objects.toString(row.get("file_path"), ""));
            result.add(pharmacy);
        }

        return result;
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
