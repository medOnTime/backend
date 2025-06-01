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
        Query query = entityManager.createNativeQuery("select medicine_id,medicine_name,description from medicines where medicine_id = :medicineId", MedicineDTO.class);
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
                "SELECT inventry_id, medicine_id, quantity, start_date, end_date FROM medicine_inventry WHERE user_id = :userId",
                Tuple.class
        );
        query.setParameter("userId", userId);

        List<Tuple> tupleList = query.getResultList();
        List<HashMap<String, String>> inventoryList = new ArrayList<>();

        if (tupleList == null || tupleList.isEmpty()) {
            return inventoryList; // returns empty list if no results
        }

        for (Tuple tuple : tupleList) {
            HashMap<String, String> inventory = new HashMap<>();

            inventory.put("inventoryId", tuple.get("inventry_id") == null ? null : tuple.get("inventry_id").toString());
            inventory.put("medicineId", tuple.get("medicine_id") == null ? null : tuple.get("medicine_id").toString());
            inventory.put("quantity", tuple.get("quantity") == null ? null : tuple.get("quantity").toString());
            inventory.put("startDate", tuple.get("start_date") == null ? null : tuple.get("start_date").toString());
            inventory.put("endDate", tuple.get("end_date") == null ? null : tuple.get("end_date").toString());

            inventoryList.add(inventory);
        }

        return inventoryList;
    }

    @Override
    public HashMap<String, String> getMedicineInventoryByUserIdAndMedicineID(int userId, int medicineId) {
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT inventry_id, medicine_id, quantity, start_date, end_date " +
                            "FROM medicine_inventry WHERE user_id = :userId AND medicine_id = :medicineId",
                    Tuple.class
            );
            query.setParameter("userId", userId);
            query.setParameter("medicineId", medicineId);

            Tuple tuple = (Tuple) query.getSingleResult();

            HashMap<String, String> inventory = new HashMap<>();
            inventory.put("inventoryId", tuple.get("inventry_id") == null ? null : tuple.get("inventry_id").toString());
            inventory.put("medicineId", tuple.get("medicine_id") == null ? null : tuple.get("medicine_id").toString());
            inventory.put("quantity", tuple.get("quantity") == null ? null : tuple.get("quantity").toString());
            inventory.put("startDate", tuple.get("start_date") == null ? null : tuple.get("start_date").toString());
            inventory.put("endDate", tuple.get("end_date") == null ? null : tuple.get("end_date").toString());

            return inventory;

        } catch (NoResultException e) {
            // Return empty map if no result found
            return new HashMap<>();
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
