package com.MedOnTime.chatBotService.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ChatBotServiceRepositoryImpl implements ChatBotServiceRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<HashMap<String, String>> findScheduledRemindersWithFilters(
            Integer userId,
            @Nullable String status,
            @Nullable LocalDate date) {

        StringBuilder baseSql = new StringBuilder(
                "FROM reminder_scheduler rs " +
                        "JOIN reminders r ON rs.reminder_id = r.reminder_id " +
                        "WHERE r.user_id = :userId "
        );

        if (status != null && !status.isEmpty()) {
            baseSql.append("AND rs.status = :status ");
        }

        if (date != null) {
            baseSql.append("AND CAST(rs.scheduled_time AS DATE) = :scheduledDate ");
        }


        String selectSql =
                "SELECT r.reminder_id, rs.schedule_id, rs.scheduled_time, r.medicine_name, rs.dosage, " +
                        "r.medicine_type, rs.status, rs.taken_time, r.strength " + baseSql +
                        " ORDER BY rs.scheduled_time";

        Query dataQuery = entityManager.createNativeQuery(selectSql);
        dataQuery.setParameter("userId", userId);
        if (status != null && !status.isEmpty()) dataQuery.setParameter("status", status);
        if (date != null) dataQuery.setParameter("scheduledDate", date);

        List<Object[]> results = dataQuery.getResultList();
        List<HashMap<String, String>> content = new ArrayList<>();

        for (Object[] row : results) {
            HashMap<String, String> map = new HashMap<>();
            map.put("reminderId", row[0] != null ? row[0].toString() : null);
            map.put("scheduleId", row[1] != null ? row[1].toString() : null);
            map.put("scheduleDateAndTime", row[2] != null ? row[2].toString() : null);
            map.put("medicineName", row[3] != null ? row[3].toString() : null);
            map.put("dosage", row[4] != null ? row[4].toString() : null);
            map.put("medicineType", row[5] != null ? row[5].toString() : null);
            map.put("status", row[6] != null ? row[6].toString() : null);
            map.put("takenDateAndTime", row[7] != null ? row[7].toString() : null);
            map.put("medicineStrength", row[8] != null ? row[8].toString() : null);
            content.add(map);
        }

        return content;
    }

    @Override
    public List<HashMap<String, String>> getMedicineInventoryByUser(int userId) {
        Query query = entityManager.createNativeQuery(
                "SELECT i.inventry_id, i.medicine_id, i.quantity, i.start_date, i.end_date, " +
                        "m.medicine_name, m.description, m.type, m.strength " +
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
            inventory.put("strength", tuple.get("strength") != null ? tuple.get("strength").toString() : null);

            inventoryList.add(inventory);
        }

        return inventoryList;
    }


}
