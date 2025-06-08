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
    public Page<ReminderSchedulesDTO> findScheduledRemindersWithFilters(String userId, @Nullable Map<String,String> filter, int page, int size) {
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

    @Override
    public List<ReminderDTO> getRemindersByFilterForUpdate(String userId) {

        Integer userIdInt = Integer.parseInt(userId);
        LocalDateTime checkedDateTime = LocalDateTime.now();

        return reminderServiceRepository.getRemindersByFilter(userIdInt,checkedDateTime,null);

    }

    @Override
    @Transactional
    public String updateReminder(ReminderDTO newReminderDTO) throws Exception {
        ReminderDTO existingReminder = reminderServiceRepository.getRemindersByFilter(null,null,Integer.parseInt(newReminderDTO.getReminderId())).get(0);

        if (existingReminder == null) {
            throw new Exception("Reminder not found.");
        }

        // Normalize and compare fields (case-insensitive, trim spaces)
        boolean isSameReminder = isEqualIgnoringCase(existingReminder.getMedicineName(), newReminderDTO.getMedicineName()) &&
                isEqualIgnoringCase(existingReminder.getMedicineType(), newReminderDTO.getMedicineType()) &&
                isEqualIgnoringCase(existingReminder.getStrength(), newReminderDTO.getStrength()) &&
                existingReminder.getTimesPerDay().equals(newReminderDTO.getTimesPerDay()) &&
                existingReminder.getDosageList().equals(newReminderDTO.getDosageList());

        List<ReminderSchedulesDTO> currentSchedules = reminderServiceRepository
                .getScheduleListByReminderId(Integer.parseInt(newReminderDTO.getReminderId()));

        boolean hasTakenSchedules = currentSchedules.stream()
                .anyMatch(schedule -> schedule.getStatus() == ScheduleStatus.TAKEN);

        int currentNumberOfDays = existingReminder.getNumberOfDays();
        int newNumberOfDays = newReminderDTO.getNumberOfDays();

        if (!isSameReminder && hasTakenSchedules) {
            throw new Exception("Reminder cannot be fully updated as some schedules have already been taken.");
        }

        if (hasTakenSchedules && currentNumberOfDays != newNumberOfDays) {
            if (newNumberOfDays < currentNumberOfDays) {
                // REDUCE number of days
                int expectedPendingCount = newReminderDTO.getTimesPerDay() * (currentNumberOfDays - newNumberOfDays);

                List<ReminderSchedulesDTO> pendingSchedules = currentSchedules.stream()
                        .filter(s -> s.getStatus() == ScheduleStatus.PENDING)
                        .collect(Collectors.toList());

                if (pendingSchedules.size() < expectedPendingCount) {
                    throw new Exception("Cannot reduce days because not enough pending schedules exist.");
                }

                // Cancel excess pending schedules
                List<ReminderSchedulesDTO> toCancel = pendingSchedules.subList(0, expectedPendingCount);
                toCancel.forEach(s -> {
                    s.setStatus(ScheduleStatus.CANCEL);
                    reminderServiceRepository.updateScheduleStatus(s);
                });

                // Update reminder
                newReminderDTO.setEndDate(newReminderDTO.getStartDate().plusDays(newNumberOfDays - 1));
                reminderServiceRepository.updateReminder(newReminderDTO);

                return "Reminder partially updated (number of days reduced).";

            } else {
                // INCREASE number of days
                int extraDays = newNumberOfDays - currentNumberOfDays;
                int intervalHours = 24 / newReminderDTO.getTimesPerDay();
                LocalDateTime baseTime = newReminderDTO.getStartDate().plusDays(currentNumberOfDays);

                List<ReminderSchedulesDTO> newSchedules = new ArrayList<>();

                for (int day = 0; day < extraDays; day++) {
                    for (int time = 0; time < newReminderDTO.getTimesPerDay(); time++) {
                        LocalDateTime scheduledTime = baseTime.plusDays(day).plusHours(time * intervalHours);

                        ReminderSchedulesDTO schedule = ReminderSchedulesDTO.builder()
                                .reminderId(newReminderDTO.getReminderId())
                                .scheduleDateAndTime(scheduledTime)
                                .dosage(newReminderDTO.getDosageList().get(
                                        newReminderDTO.getDosageList().size() == 1 ? 0 : time))
                                .status(ScheduleStatus.PENDING)
                                .takenDateAndTime(null)
                                .build();

                        newSchedules.add(schedule);

                        if (scheduledTime.toLocalDate().isEqual(LocalDate.now())) {
                            reminderServiceRepository.addScheduleForTempTable(schedule);
                        }
                    }
                }

                // Update reminder
                newReminderDTO.setEndDate(newReminderDTO.getStartDate().plusDays(newNumberOfDays - 1));
                reminderServiceRepository.updateReminder(newReminderDTO);

                // Save new schedules
                newSchedules.forEach(reminderServiceRepository::addSchedule);

                return "Reminder partially updated (number of days increased).";
            }
        }


        // Full update allowed
        // Cancel all current schedules
        currentSchedules.forEach(s -> {
            s.setStatus(ScheduleStatus.CANCEL);
            reminderServiceRepository.updateScheduleStatus(s);
        });

        // Update reminder
        newReminderDTO.setEndDate(newReminderDTO.getStartDate().plusDays(newNumberOfDays - 1));
        String dosageString = newReminderDTO.getDosageList().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        newReminderDTO.setDosageString(dosageString);
        reminderServiceRepository.updateReminder(newReminderDTO);

        // Add new schedules
        List<ReminderSchedulesDTO> newSchedules = new ArrayList<>();
        int intervalHours = 24 / newReminderDTO.getTimesPerDay();
        LocalDateTime baseTime = newReminderDTO.getStartDate();

        for (int day = 0; day < newNumberOfDays; day++) {
            for (int time = 0; time < newReminderDTO.getTimesPerDay(); time++) {
                LocalDateTime scheduledTime = baseTime.plusDays(day).plusHours(time * intervalHours);

                ReminderSchedulesDTO schedule = ReminderSchedulesDTO.builder()
                        .reminderId(newReminderDTO.getReminderId())
                        .scheduleDateAndTime(scheduledTime)
                        .dosage(newReminderDTO.getDosageList().get(
                                newReminderDTO.getDosageList().size() == 1 ? 0 : time))
                        .status(ScheduleStatus.PENDING)
                        .takenDateAndTime(null)
                        .build();

                newSchedules.add(schedule);

                if (scheduledTime.toLocalDate().isEqual(LocalDate.now())) {
                    reminderServiceRepository.addScheduleForTempTable(schedule);
                }
            }
        }

        newSchedules.forEach(reminderServiceRepository::addSchedule);
        return "Reminder fully updated with new schedules.";
    }

    private boolean isEqualIgnoringCase(String s1, String s2) {
        if (s1 == null || s2 == null) return false;
        return s1.trim().equalsIgnoreCase(s2.trim());
    }


}
