package com.medOnTime.reminderService.service;

import com.medOnTime.reminderService.dto.ReminderDTO;
import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import com.medOnTime.reminderService.dto.ScheduleStatus;
import com.medOnTime.reminderService.repository.ReminderServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReminderScheduleServiceImpl implements ReminderScheduleService{

    @Autowired
    private ReminderServiceRepository reminderServiceRepository;

    @Override
    @Transactional
    public String addReminder(ReminderDTO reminderDTO) throws Exception {
        if (reminderDTO.getStartDate() == null || reminderDTO.getNumberOfDays() == null || reminderDTO.getHours() == null) {
            throw new Exception("Start date, number of days, and hours are required.");
        }

        // Calculate end date
        LocalDateTime endDate = reminderDTO.getStartDate().plusDays(reminderDTO.getNumberOfDays());
        reminderDTO.setEndDate(endDate);

        if (reminderServiceRepository.getReminderId(reminderDTO) != null){
            throw new Exception("This reminder is already added");
        }

        // Save reminder
        String message = reminderServiceRepository.addReminder(reminderDTO);

        // Generate schedule list
        List<ReminderSchedulesDTO> schedules = new ArrayList<>();
        LocalDateTime current = reminderDTO.getStartDate();

        String reminderId = reminderServiceRepository.getReminderId(reminderDTO);

        while (!current.isAfter(endDate)) {
            ReminderSchedulesDTO schedule = new ReminderSchedulesDTO();
            schedule.setReminderId(reminderId);
            schedule.setScheduleDateAndTime(current);
            schedule.setStatus(ScheduleStatus.PENDING);
            schedule.setTakenDateAndTime(null);

            schedules.add(schedule);
            current = current.plusHours(reminderDTO.getHours());
        }

        schedules.forEach(s -> reminderServiceRepository.addSchedule(s));

        return "Reminder added with " + schedules.size() + " schedules.";
    }


}
