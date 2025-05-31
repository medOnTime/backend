package com.medOnTime.medicineService.repo;

import com.medOnTime.medicineService.dto.MedicineDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class MedicineRepositoryImpl implements MedicineRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MedicineDTO> getAllMedicines(){

        Query query = entityManager.createNativeQuery("select medicine_id,medicine_name,description from medicines", MedicineDTO.class);

        List<MedicineDTO> medicineDTOS = query.getResultList();

        return medicineDTOS;
    }

    @Override
    @Transactional
    public String addMedicineToInventry(HashMap<String, String> addedMedicine) {

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

        }

        return messaege;
    }
}
