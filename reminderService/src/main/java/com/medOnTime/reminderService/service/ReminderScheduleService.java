package com.medOnTime.reminderService.service;

import com.medOnTime.reminderService.dto.ReminderDTO;
import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface ReminderScheduleService {

    public String addReminder(ReminderDTO reminderDTO) throws Exception;

    Page<ReminderSchedulesDTO> getUserRemindersByFiltering(String userId, @Nullable Map<String,String> filter, int page, int size);

    List<ReminderDTO> getRemindersByFilterForUpdate(String userId);

}
