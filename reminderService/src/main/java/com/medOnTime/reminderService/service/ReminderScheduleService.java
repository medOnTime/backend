package com.medOnTime.reminderService.service;

import com.medOnTime.reminderService.dto.ReminderDTO;

public interface ReminderScheduleService {

    public String addReminder(ReminderDTO reminderDTO) throws Exception;

}
