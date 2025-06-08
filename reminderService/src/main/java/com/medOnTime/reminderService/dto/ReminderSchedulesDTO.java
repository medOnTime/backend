package com.medOnTime.reminderService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class ReminderSchedulesDTO {

    private String scheduleId;
    private String reminderId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduleDateAndTime;
    private String medicineName;
    private ScheduleStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime takenDateAndTime;
    private Integer dosage;
    private String medicineType;
    private String medicineStrength;
}
