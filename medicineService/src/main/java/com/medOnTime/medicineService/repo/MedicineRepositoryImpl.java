package com.medOnTime.medicineService.repo;

import com.medOnTime.medicineService.dto.MedicineDTO;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class MedicineRepositoryImpl implements MedicineRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MedicineDTO getMedicineDetailById(int medicineId){
        Query query = entityManager.createNativeQuery("select medicine_id,medicine_name,description,type from medicines where medicine_id = :medicineId", MedicineDTO.class);
        query.setParameter("medicineId", medicineId);

        MedicineDTO medicineDTO = (MedicineDTO) query.getSingleResult();

        return medicineDTO;
    }

    @Override
    public List<MedicineDTO> getAllMedicines(){

        Query query = entityManager.createNativeQuery("select medicine_id,medicine_name,description from medicines", MedicineDTO.class);

        List<MedicineDTO> medicineDTOS = query.getResultList();

        return medicineDTOS;
    }

    @Override
    @Transactional
    public String addMedicineToInventry(HashMap<String, String> addedMedicine) throws Exception {

        String messaege = "";

        try {
            Query query = entityManager.createNativeQuery("insert into medicine_inventry (user_id,medicine_id,quantity,start_date,end_date) values (:userId, :medicineId, :quantity, :startDate, :endDate)")
                    .setParameter("userId", addedMedicine.get("userId"))
                    .setParameter("medicineId", addedMedicine.get("medicineId"))
                    .setParameter("quantity", addedMedicine.get("quantity"))
                    .setParameter("startDate", addedMedicine.get("startDate"))
                    .setParameter("endDate", addedMedicine.get("endDate"));

            query.executeUpdate();
            messaege = "Medicine Succesfully added to the database";

        } catch (Exception e){
            throw new Exception(e.getMessage());
        }

        return messaege;
    }

    @Override
    public List<HashMap<String, String>> getMedicineInventoryByUser(int userId) {
        Query query = entityManager.createNativeQuery(
                "SELECT i.inventry_id, i.medicine_id, i.quantity, i.start_date, i.end_date, " +
                        "m.medicine_name, m.description, m.type " +
                        "FROM medicine_inventry i " +
                        "JOIN medicines m ON i.medicine_id = m.medicine_id " +
                        "WHERE i.user_id = :userId",
                Tuple.class
        );
        query.setParameter("userId", userId);

        List<Tuple> tupleList = query.getResultList();
        List<HashMap<String, String>> inventoryList = new ArrayList<>();

        if (tupleList == null || tupleList.isEmpty()) {
            return inventoryList;
        }

        for (Tuple tuple : tupleList) {
            HashMap<String, String> inventory = new HashMap<>();

            inventory.put("inventoryId", tuple.get("inventry_id") != null ? tuple.get("inventry_id").toString() : null);
            inventory.put("medicineId", tuple.get("medicine_id") != null ? tuple.get("medicine_id").toString() : null);
            inventory.put("quantity", tuple.get("quantity") != null ? tuple.get("quantity").toString() : null);
            inventory.put("startDate", tuple.get("start_date") != null ? tuple.get("start_date").toString() : null);
            inventory.put("endDate", tuple.get("end_date") != null ? tuple.get("end_date").toString() : null);
            inventory.put("medicineName", tuple.get("medicine_name") != null ? tuple.get("medicine_name").toString() : null);
            inventory.put("description", tuple.get("description") != null ? tuple.get("description").toString() : null);
            inventory.put("type", tuple.get("type") != null ? tuple.get("type").toString() : null);

            inventoryList.add(inventory);
        }

        return inventoryList;
    }


    @Override
    public HashMap<String, String> getMedicineInventoryByUserIdAndMedicineID(int userId, int medicineId) {
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT i.inventry_id, i.medicine_id, i.quantity, i.start_date, i.end_date, " +
                            "m.medicine_name, m.description, m.type " +
                            "FROM medicine_inventry i " +
                            "JOIN medicines m ON i.medicine_id = m.medicine_id " +
                            "WHERE i.user_id = :userId AND i.medicine_id = :medicineId",
                    Tuple.class
            );
            query.setParameter("userId", userId);
            query.setParameter("medicineId", medicineId);

            Tuple tuple = (Tuple) query.getSingleResult();

            HashMap<String, String> inventory = new HashMap<>();
            inventory.put("inventoryId", tuple.get("inventry_id") != null ? tuple.get("inventry_id").toString() : null);
            inventory.put("medicineId", tuple.get("medicine_id") != null ? tuple.get("medicine_id").toString() : null);
            inventory.put("quantity", tuple.get("quantity") != null ? tuple.get("quantity").toString() : null);
            inventory.put("startDate", tuple.get("start_date") != null ? tuple.get("start_date").toString() : null);
            inventory.put("endDate", tuple.get("end_date") != null ? tuple.get("end_date").toString() : null);
            inventory.put("medicineName", tuple.get("medicine_name") != null ? tuple.get("medicine_name").toString() : null);
            inventory.put("description", tuple.get("description") != null ? tuple.get("description").toString() : null);
            inventory.put("type", tuple.get("type") != null ? tuple.get("type").toString() : null);

            return inventory;

        } catch (NoResultException e) {
            return new HashMap<>(); // return empty if not found
        }
    }


    @Override
    @Transactional
    public String updateMedicineInventory(HashMap<String, String> updatedMedicineInventory) {
        Query query = entityManager.createNativeQuery(
                "UPDATE medicine_inventry SET quantity = :quantity, start_date = :startDate, end_date = :endDate WHERE inventry_id = :inventoryId"
        );

        query.setParameter("quantity", Integer.parseInt(updatedMedicineInventory.get("quantity")));
        query.setParameter("startDate", java.sql.Date.valueOf(updatedMedicineInventory.get("startDate")));
        query.setParameter("endDate", java.sql.Date.valueOf(updatedMedicineInventory.get("endDate")));
        query.setParameter("inventoryId", Integer.parseInt(updatedMedicineInventory.get("inventoryId")));

        int rowsUpdated = query.executeUpdate();
        return rowsUpdated > 0 ? "Update successful" : "No records updated";
    }

}
