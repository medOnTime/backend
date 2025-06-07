package com.medOnTime.reminderService.repository;

import com.medOnTime.reminderService.dto.ReminderDTO;
import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReminderServiceRepository {

    String addReminder(ReminderDTO reminderDTO);

    void addScheduleForTempTable(ReminderSchedulesDTO reminderSchedulesDTO);

    String getReminderId(ReminderDTO reminderDTO);

    void addSchedule(ReminderSchedulesDTO reminderSchedulesDTO);

    Page<ReminderSchedulesDTO> findScheduledRemindersWithFilters(Integer userId, @Nullable String status, @Nullable LocalDate date, @Nullable Integer reminderId, Pageable pageable);

    List<ReminderDTO> getRemindersByFilterForUpdate(Integer userId, @Nullable LocalDateTime checkedDateTime);

}
