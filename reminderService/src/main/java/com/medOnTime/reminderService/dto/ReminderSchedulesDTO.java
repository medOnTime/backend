package com.medOnTime.reminderService.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ReminderSchedulesDTO {

    private String scheduleId;
    private String reminderId;
    private LocalDateTime scheduleDateAndTime;
    private ScheduleStatus status;
    private LocalDateTime takenDateAndTime;

}
