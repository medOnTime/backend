package com.medOnTime.reminderService.repository;

import com.medOnTime.reminderService.dto.ReminderDTO;
import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReminderServiceRepositoryImpl implements ReminderServiceRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public String addReminder(ReminderDTO reminderDTO) {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO reminders " +
                        "(user_id, medicine_id, dosage, times_per_day, start_date, end_date, number_of_days, medicine_name, medicine_type, strength) " +
                        "VALUES (:userId, :medicineId, :dosage, :timesPerDay, :startDate, :endDate, :numberOfDays, :medicineName, :medicineType, :strength)"
        );

        query.setParameter("userId", reminderDTO.getUserId());
        query.setParameter("medicineId", reminderDTO.getMedicineId());
        query.setParameter("dosage", reminderDTO.getDosageString()); // <-- pass as string
        query.setParameter("timesPerDay", reminderDTO.getTimesPerDay());
        query.setParameter("startDate", reminderDTO.getStartDate());
        query.setParameter("endDate", reminderDTO.getEndDate());
        query.setParameter("numberOfDays", reminderDTO.getNumberOfDays());
        query.setParameter("medicineName", reminderDTO.getMedicineName());
        query.setParameter("medicineType", reminderDTO.getMedicineType());
        query.setParameter("strength", reminderDTO.getStrength());

        query.executeUpdate();

        return "Reminder successfully added";
    }



    @Override
    public String getReminderId(ReminderDTO reminderDTO) {
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT reminder_id FROM reminders " +
                            "WHERE user_id = :userId " +
                            "AND medicine_id = :medicineId " +
                            "AND CAST(start_date AS DATE) = CAST(:startDate AS DATE)"
            );

            query.setParameter("userId", Integer.parseInt(reminderDTO.getUserId()));
            query.setParameter("medicineId", Integer.parseInt(reminderDTO.getMedicineId()));
            query.setParameter("startDate", reminderDTO.getStartDate());

            Object result = query.getSingleResult();
            return result.toString();
        } catch (NoResultException e){
            return null;
        }
    }


    @Override
    @Transactional
    public void addSchedule(ReminderSchedulesDTO reminderSchedulesDTO) {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO reminder_scheduler (reminder_id, scheduled_time, status, taken_time, dosage) " +
                        "VALUES (:reminderId, :scheduledTime, :status, :takenTime, :dosage)"
        );

        query.setParameter("reminderId", Integer.parseInt(reminderSchedulesDTO.getReminderId()));
        query.setParameter("scheduledTime", reminderSchedulesDTO.getScheduleDateAndTime());
        query.setParameter("status", reminderSchedulesDTO.getStatus().toString());
        query.setParameter("takenTime", reminderSchedulesDTO.getTakenDateAndTime());
        query.setParameter("dosage", reminderSchedulesDTO.getDosage());

        query.executeUpdate();
    }


    @Override
    public List<Map<String, String>> getScheduledReminderDetailsByUserAndStatus(Integer userId, @Nullable String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT rs.scheduled_time, r.medicine_name, r.dosage, r.medicine_type, rs.status " +
                        "FROM reminder_schedules rs " +
                        "JOIN reminders r ON rs.reminder_id = r.reminder_id " +
                        "WHERE r.user_id = :userId "
        );

        if (status != null && !status.isEmpty()) {
            sql.append("AND rs.status = :status ");
        }

        sql.append("ORDER BY rs.scheduled_time");

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("userId", userId);

        if (status != null && !status.isEmpty()) {
            query.setParameter("status", status);
        }

        List<Object[]> results = query.getResultList();

        List<Map<String, String>> scheduledReminders = new ArrayList<>();

        for (Object[] row : results) {
            Timestamp scheduledTimestamp = (Timestamp) row[0];
            String medicineName = (String) row[1];
            Integer dosage = (Integer) row[2];
            String medicineType = (String) row[3];
            String reminderStatus = (String) row[4];

            Map<String, String> reminderDetails = new LinkedHashMap<>();
            reminderDetails.put("Medicine", medicineName);
            reminderDetails.put("Dosage", dosage.toString());
            reminderDetails.put("Type", medicineType);
            reminderDetails.put("Status", reminderStatus);
            reminderDetails.put("ScheduledTime", scheduledTimestamp.toString());

            scheduledReminders.add(reminderDetails);
        }

        return scheduledReminders;
    }




}
