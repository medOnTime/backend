package com.medOnTime.reminderService.repository;

import com.medOnTime.reminderService.dto.ReminderDTO;
import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Map;

public interface ReminderServiceRepository {

    String addReminder(ReminderDTO reminderDTO);;

    String getReminderId(ReminderDTO reminderDTO);

    void addSchedule(ReminderSchedulesDTO reminderSchedulesDTO);

    List<Map<String, String>> getScheduledReminderDetailsByUserAndStatus(Integer userId, String status);

}
