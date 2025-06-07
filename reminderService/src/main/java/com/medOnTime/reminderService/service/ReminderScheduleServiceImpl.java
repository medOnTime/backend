package com.medOnTime.reminderService.service;

import com.medOnTime.reminderService.dto.ReminderDTO;
import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import com.medOnTime.reminderService.dto.ScheduleStatus;
import com.medOnTime.reminderService.repository.ReminderServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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


                LocalDateTime scheduledTime = baseTime
                        .plusDays(day)
                        .plusHours(time * intervalHours);

                ReminderSchedulesDTO schedule = ReminderSchedulesDTO.builder()
                        .reminderId(reminderId)
                        .scheduleDateAndTime(scheduledTime)
                        .dosage(reminderDTO.getDosageList().get(reminderDTO.getDosageList().size() == 1? 0 : time))
                        .status(ScheduleStatus.PENDING)
                        .takenDateAndTime(null)
                        .build();

                schedules.add(schedule);

                if (scheduledTime.toLocalDate().isEqual(LocalDate.now())) {
                    reminderServiceRepository.addScheduleForTempTable(schedule);
                }

            }
        }


        schedules.forEach(reminderServiceRepository::addSchedule);

        return "Reminder added with " + schedules.size() + " schedules.";
    }

    @Override
    public Page<ReminderSchedulesDTO> getUserRemindersByFiltering(String userId, @Nullable Map<String,String> filter, int page, int size) {
        Integer intUserId = Integer.parseInt(userId);

        String status = null;
        String dateStr = null;
        Integer reminderId = null;

        if (filter != null) {
            status = filter.getOrDefault("status", null);
            dateStr = filter.getOrDefault("date", null);

            String reminderIdStr = filter.getOrDefault("reminderId", null);
            if (reminderIdStr != null && !reminderIdStr.isEmpty()) {
                reminderId = Integer.parseInt(reminderIdStr);
            }
        }

        LocalDate dateTime = null;
        if (dateStr != null && !dateStr.isEmpty()) {

            dateTime = LocalDate.parse(dateStr);
        }

        Pageable pageable = PageRequest.of(page, size);

        return reminderServiceRepository.findScheduledRemindersWithFilters(intUserId, status, dateTime, reminderId, pageable);
    }

    public List<ReminderDTO> getRemindersByFilterForUpdate(String userId) {

        Integer userIdInt = Integer.parseInt(userId);
        LocalDateTime checkedDateTime = LocalDateTime.now();

        return reminderServiceRepository.getRemindersByFilterForUpdate(userIdInt,checkedDateTime);

    }

}
