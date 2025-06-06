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
import java.util.stream.Collectors;

@Service
public class ReminderScheduleServiceImpl implements ReminderScheduleService{

    @Autowired
    private ReminderServiceRepository reminderServiceRepository;

    @Override
    @Transactional
    public String addReminder(ReminderDTO reminderDTO) throws Exception {
        if (reminderDTO.getStartDate() == null || reminderDTO.getNumberOfDays() == 0 || reminderDTO.getDosageList() == null) {
            throw new Exception("Start date, number of days, and dosage list are required.");
        }

        if (reminderDTO.getDosageList().size() != reminderDTO.getTimesPerDay() && reminderDTO.getDosageList().size() != 1) {
            throw new Exception("Dosage list size must match times per day.");
        }

        // Calculate end date
        LocalDateTime endDate = reminderDTO.getStartDate().plusDays(reminderDTO.getNumberOfDays() - 1);
        reminderDTO.setEndDate(endDate);

        // Check if already exists
        if (reminderServiceRepository.getReminderId(reminderDTO) != null){
            throw new Exception("This reminder is already added");
        }

        String dosageString = reminderDTO.getDosageList().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        reminderDTO.setDosageString(dosageString);

        // Save reminder
        String message = reminderServiceRepository.addReminder(reminderDTO);

        // Get inserted reminder ID
        String reminderId = reminderServiceRepository.getReminderId(reminderDTO);

        // Prepare schedules
        List<ReminderSchedulesDTO> schedules = new ArrayList<>();
        int intervalHours = 24 / reminderDTO.getTimesPerDay();

        LocalDateTime baseTime = reminderDTO.getStartDate(); // this already has both date and time

        for (int day = 0; day < reminderDTO.getNumberOfDays(); day++) {
            for (int time = 0; time < reminderDTO.getTimesPerDay(); time++) {
                ReminderSchedulesDTO schedule = new ReminderSchedulesDTO();

                LocalDateTime scheduledTime = baseTime
                        .plusDays(day)
                        .plusHours(time * intervalHours);

                schedule.setReminderId(reminderId);
                schedule.setScheduleDateAndTime(scheduledTime);
                schedule.setDosage(reminderDTO.getDosageList().get(reminderDTO.getDosageList().size() == 1? 0 : time));
                schedule.setStatus(ScheduleStatus.PENDING);
                schedule.setTakenDateAndTime(null);

                schedules.add(schedule);
            }
        }


        schedules.forEach(reminderServiceRepository::addSchedule);

        return "Reminder added with " + schedules.size() + " schedules.";
    }



}
