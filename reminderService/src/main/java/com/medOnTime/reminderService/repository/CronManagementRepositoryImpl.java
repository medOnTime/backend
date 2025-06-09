package com.medOnTime.reminderService.repository;

import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import com.medOnTime.reminderService.dto.ScheduleStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CronManagementRepositoryImpl implements CronManagementRepository {

    private static final Logger logger = LoggerFactory.getLogger(CronManagementRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ReminderSchedulesDTO> getTodayReminderSchedules(LocalDate today) {
        String sql = "SELECT schedule_id, reminder_id, scheduled_time, status, taken_time, dosage " +
                "FROM reminder_scheduler " +
                "WHERE CAST(scheduled_time AS DATE) = :today";

        List<Tuple> tuples = entityManager.createNativeQuery(sql, Tuple.class)
                .setParameter("today", today)
                .getResultList();

        List<ReminderSchedulesDTO> result = new ArrayList<>();

        for (Tuple tuple : tuples) {
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

        logger.info("Complete getting today schedules: " + LocalDate.now() + " with size of: " + result.size());

        return result;
    }

    @Override
    @Transactional
    public void insertToTempScheduler(ReminderSchedulesDTO dto) {
        String sql = "INSERT INTO temp_scheduler " +
                "(reminder_id, scheduled_time, status, taken_time, dosage) " +
                "VALUES (:reminderId, :scheduledTime, :status, :takenTime, :dosage)";

        entityManager.createNativeQuery(sql)
                .setParameter("reminderId", Integer.parseInt(dto.getReminderId()))
                .setParameter("scheduledTime", dto.getScheduleDateAndTime())
                .setParameter("status", dto.getStatus().toString())
                .setParameter("takenTime", dto.getTakenDateAndTime())
                .setParameter("dosage", dto.getDosage())
                .executeUpdate();
    }

    @Override
    @Transactional
    public void deleteOldTempSchedules(LocalDateTime beforeDateTime) {
        String sql = "DELETE FROM temp_scheduler WHERE scheduled_time < :beforeTime";
        entityManager.createNativeQuery(sql)
                .setParameter("beforeTime", beforeDateTime)
                .executeUpdate();
    }

    @Override
    public List<ReminderSchedulesDTO> findDueReminders(LocalDateTime now) {

        LocalDateTime nextMinute = now.plusMinutes(1);

        String sql = "SELECT r.user_id, r.reminder_id, ts.schedule_id, ts.scheduled_time, r.medicine_id, " +
                "r.medicine_name, ts.dosage, r.medicine_type, ts.status, ts.taken_time, r.strength " +
                "FROM temp_scheduler ts " +
                "JOIN reminders r ON ts.reminder_id = r.reminder_id " +
                "WHERE ts.scheduled_time >= :startOfMinute AND ts.scheduled_time < :startOfNextMinute\n " +
                "AND ts.status = 'PENDING' " +
                "ORDER BY ts.scheduled_time";

        List<Tuple> results = entityManager.createNativeQuery(sql, Tuple.class)
                .setParameter("startOfMinute", now)
                .setParameter("startOfNextMinute", nextMinute)
                .getResultList();

        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results.stream().map(t -> ReminderSchedulesDTO.builder()
                .reminderId(t.get("reminder_id").toString())
                .scheduleId(t.get("schedule_id").toString())
                .userId(t.get("user_id").toString())
                .scheduleDateAndTime(t.get("scheduled_time") != null
                        ? ((Timestamp) t.get("scheduled_time")).toLocalDateTime()
                        : null)
                .medicineId(t.get("medicine_id").toString())
                .medicineName(t.get("medicine_name").toString())
                .dosage(t.get("dosage") != null ? Integer.parseInt(t.get("dosage").toString()) : 0)
                .medicineType(t.get("medicine_type").toString())
                .status(t.get("status") != null ? ScheduleStatus.valueOf((String) t.get("status")) : null)
                .takenDateAndTime(t.get("taken_time") != null
                        ? ((Timestamp) t.get("taken_time")).toLocalDateTime()
                        : null)
                .medicineStrength(t.get("strength") != null ? t.get("strength").toString() : null)
                .build()).collect(Collectors.toList());
    }


}
