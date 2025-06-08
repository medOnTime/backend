package com.medOnTime.reminderService.repository;

import com.medOnTime.reminderService.dto.ReminderDTO;
import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import com.medOnTime.reminderService.dto.ScheduleStatus;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    // for today reminders only
    @Override
    @Transactional
    public void addScheduleForTempTable(ReminderSchedulesDTO reminderSchedulesDTO) {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO temp_scheduler (reminder_id, scheduled_time, status, taken_time, dosage) " +
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
    public Page<ReminderSchedulesDTO> findScheduledRemindersWithFilters(
            Integer userId,
            @Nullable String status,
            @Nullable LocalDate date,
            @Nullable Integer reminderId,
            Pageable pageable) {

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

        if (reminderId != null){
            baseSql.append("AND r.reminder_id = :reminderId ");
        }

        // Count query
        String countQueryStr = "SELECT COUNT(*) " + baseSql;
        Query countQuery = entityManager.createNativeQuery(countQueryStr);
        countQuery.setParameter("userId", userId);
        if (status != null && !status.isEmpty()) countQuery.setParameter("status", status);
        if (date != null) countQuery.setParameter("scheduledDate", date);
        if (reminderId != null) countQuery.setParameter("reminderId", reminderId);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        // Data query
        String selectSql = "SELECT r.reminder_id, rs.schedule_id, rs.scheduled_time, r.medicine_name, rs.dosage, r.medicine_type, rs.status, rs.taken_time, r.strength " + baseSql + " ORDER BY rs.scheduled_time";

        Query dataQuery = entityManager.createNativeQuery(selectSql);
        dataQuery.setParameter("userId", userId);
        if (status != null && !status.isEmpty()) dataQuery.setParameter("status", status);
        if (date != null) dataQuery.setParameter("scheduledDate", date);
        if (reminderId != null) dataQuery.setParameter("reminderId", reminderId);
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        List<Object[]> results = dataQuery.getResultList();

        List<ReminderSchedulesDTO> content = results.stream().map(row -> {
            // Map manually or create a utility
            return ReminderSchedulesDTO.builder()
                    .scheduleId(row[1] != null ? row[1].toString() : null)
                    .reminderId(row[0] != null ? row[0].toString() : null)
                    .scheduleDateAndTime(row[2] != null ? ((Timestamp) row[2]).toLocalDateTime() : null)
                    .medicineName(row[3] != null ? (String) row[3] : null)
                    .dosage(Integer.parseInt(row[4] != null ? row[4].toString() : "0"))
                    .medicineType(row[5] != null ? (String) row[5] : null)
                    .status(row[6] != null ? ScheduleStatus.valueOf((String) row[6]) : null)
                    .takenDateAndTime(row[7] != null ? ((Timestamp) row[7]).toLocalDateTime() : null)
                    .medicineStrength(row[8] != null ? row[8].toString() : null)
                    .build();
        }).collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<ReminderDTO> getRemindersByFilter(@Nullable Integer userId, @Nullable LocalDateTime checkedDateTime, @Nullable Integer reminderId) {

        StringBuilder sql = new StringBuilder(
                "SELECT reminder_id, user_id, medicine_id, medicine_name, " +
                        "medicine_type, strength, dosage, times_per_day, start_date, number_of_days, end_date " +
                        "FROM reminders WHERE 1=1 "
        );

        Map<String, Object> parameters = new HashMap<>();

        if (userId != null) {
            sql.append("AND user_id = :userId ");
            parameters.put("userId", userId);
        }

        if (checkedDateTime != null) {
            sql.append("AND end_date >= :checkedDateTime ");
            parameters.put("checkedDateTime", checkedDateTime);
        }

        if (reminderId != null) {
            sql.append("AND reminder_id = :reminderId ");
            parameters.put("reminderId", reminderId);
        }

        Query query = entityManager.createNativeQuery(sql.toString(), Tuple.class);

        // Set parameters dynamically
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        List<Tuple> tupleList = query.getResultList();

        return tupleList.stream().map(tuple -> {
            String dosageString = tuple.get("dosage", String.class);

            List<Integer> dosageList = new ArrayList<>();
            if (dosageString != null && !dosageString.isEmpty()) {
                dosageList = Arrays.stream(dosageString.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }

            return ReminderDTO.builder()
                    .reminderId(tuple.get("reminder_id") != null ? tuple.get("reminder_id").toString() : null)
                    .userId(tuple.get("user_id") != null ? tuple.get("user_id").toString() : null)
                    .medicineId(tuple.get("medicine_id") != null ? tuple.get("medicine_id").toString() : null)
                    .medicineName(tuple.get("medicine_name", String.class))
                    .medicineType(tuple.get("medicine_type", String.class))
                    .strength(tuple.get("strength", String.class))
                    .dosageList(dosageList)
                    .timesPerDay(tuple.get("times_per_day", Integer.class))
                    .startDate(tuple.get("start_date", Timestamp.class).toLocalDateTime())
                    .numberOfDays(tuple.get("number_of_days", Integer.class))
                    .endDate(tuple.get("end_date", Timestamp.class).toLocalDateTime())
                    .build();
        }).collect(Collectors.toList());
    }


    @Override
    public List<ReminderSchedulesDTO> getScheduleListByReminderId(Integer reminderId) {
        Query query = entityManager.createNativeQuery(
                "SELECT schedule_id, reminder_id, scheduled_time, status, taken_time, dosage " +
                        "FROM reminder_scheduler WHERE reminder_id = :reminderId ORDER BY scheduled_time ASC",
                Tuple.class
        );

        query.setParameter("reminderId", reminderId);
        List<Tuple> tupleList = query.getResultList();

        List<ReminderSchedulesDTO> result = new ArrayList<>();

        for (Tuple tuple : tupleList) {
            ReminderSchedulesDTO dto = ReminderSchedulesDTO.builder()
                    .scheduleId(tuple.get("schedule_id").toString())
                    .reminderId(tuple.get("reminder_id").toString())
                    .scheduleDateAndTime(
                            tuple.get("scheduled_time") != null
                                    ? ((Timestamp) tuple.get("scheduled_time")).toLocalDateTime()
                                    : null
                    )
                    .status(
                            tuple.get("status") != null
                                    ? ScheduleStatus.valueOf(tuple.get("status").toString())
                                    : null
                    )
                    .takenDateAndTime(
                            tuple.get("taken_time") != null
                                    ? ((Timestamp) tuple.get("taken_time")).toLocalDateTime()
                                    : null
                    )
                    .dosage(tuple.get("dosage") != null ? Integer.parseInt(tuple.get("dosage").toString()) : null)
                    .build();

            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional
    public void updateReminder(ReminderDTO reminderDTO) {
        entityManager.createNativeQuery(
                        "UPDATE reminders SET " +
                                "medicine_id = :medicineId, " +
                                "medicine_name = :medicineName, " +
                                "medicine_type = :medicineType, " +
                                "strength = :strength, " +
                                "dosage = :dosageString, " +
                                "times_per_day = :timesPerDay, " +
                                "start_date = :startDate, " +
                                "number_of_days = :numberOfDays, " +
                                "end_date = :endDate " +
                                "WHERE reminder_id = :reminderId"
                )
                .setParameter("medicineId", reminderDTO.getMedicineId())
                .setParameter("medicineName", reminderDTO.getMedicineName())
                .setParameter("medicineType", reminderDTO.getMedicineType())
                .setParameter("strength", reminderDTO.getStrength())
                .setParameter("dosageString", reminderDTO.getDosageString())
                .setParameter("timesPerDay", reminderDTO.getTimesPerDay())
                .setParameter("startDate", reminderDTO.getStartDate())
                .setParameter("numberOfDays", reminderDTO.getNumberOfDays())
                .setParameter("endDate", reminderDTO.getEndDate())
                .setParameter("reminderId", reminderDTO.getReminderId())
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateScheduleStatus(ReminderSchedulesDTO schedule) {
        entityManager.createNativeQuery(
                        "UPDATE reminder_scheduler SET " +
                                "status = :status, " +
                                "taken_time = :takenDateTime " +
                                "WHERE schedule_id = :scheduleId"
                )
                .setParameter("status", schedule.getStatus().name())
                .setParameter("takenDateTime", schedule.getTakenDateAndTime())
                .setParameter("scheduleId", schedule.getScheduleId())
                .executeUpdate();
    }

    @Override
    public void deleteFromTempScheduler(String reminderId, LocalDateTime scheduleTime) {
        int deletedCount = entityManager.createNativeQuery(
                        "DELETE FROM temp_scheduler WHERE reminder_id = :reminderId AND scheduled_time = :scheduleTime"
                )
                .setParameter("reminderId", reminderId)
                .setParameter("scheduleTime", Timestamp.valueOf(scheduleTime.withNano(0)))
                .executeUpdate();

        // Optional: Log the result if needed
        if (deletedCount == 0) {
            System.out.println("No matching record found in temp_scheduler for deletion.");
        }
    }
}
