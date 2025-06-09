package com.medOnTime.reminderService.repository;

import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CronManagementRepository {

    List<ReminderSchedulesDTO> getTodayReminderSchedules(LocalDate today);

    void insertToTempScheduler(ReminderSchedulesDTO dto);

    void deleteOldTempSchedules(LocalDateTime beforeDateTime);

    List<ReminderSchedulesDTO> findDueReminders(LocalDateTime now);

}
