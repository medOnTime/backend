package com.medOnTime.reminderService.service;

import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import com.medOnTime.reminderService.repository.CronManagementRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CronManagementService {

    private static final Logger logger = LoggerFactory.getLogger(CronManagementService.class);

    @Autowired
    private CronManagementRepository cronRepo;
    @Autowired
    private MqttPublisher mqttPublisher;

//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 0 * * *") // Every midnight
    @Transactional
    public void runDailySchedulerSync() {

        logger.info("Start runDailySchedulerSync cron at: " + LocalDate.now());

        LocalDate today = LocalDate.now();
        LocalDateTime midnight = today.atStartOfDay();

        // 1. Get today's schedules
        List<ReminderSchedulesDTO> schedules = cronRepo.getTodayReminderSchedules(today);

        // 2. Insert into temp_scheduler
        for (ReminderSchedulesDTO dto : schedules) {
            cronRepo.insertToTempScheduler(dto);
        }

        // 3. Delete old entries from temp_scheduler
        cronRepo.deleteOldTempSchedules(midnight);

        logger.info("Scheduler sync complete for date: " + today + " at: " + LocalDate.now());
    }

    @Scheduled(cron = "0 * * * * *") // Every minute
    public void checkDueReminders() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0); // Truncate to seconds
        logger.info("Running scheduler at: {}", now);

        List<ReminderSchedulesDTO> dueReminders = cronRepo.findDueReminders(now);

        if (dueReminders.isEmpty()) {
            logger.info("No reminders scheduled at: {}", now);
            return;
        }

        for (ReminderSchedulesDTO dto : dueReminders) {
            String userId = dto.getUserId();
            mqttPublisher.publishReminderDto(userId, dto);
            logger.info("Due Reminder: {}", dto);
            // Add processing logic here (e.g. send push notification)
        }
        return;
    }

}
