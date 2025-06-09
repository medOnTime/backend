package com.medOnTime.medicineService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReminderSchedulesDTO {

    private String scheduleId;
    private String reminderId;
    private String userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduleDateAndTime;
    private String medicineId;
    private String medicineName;
    private ScheduleStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime takenDateAndTime;
    private Integer dosage;
    private String medicineType;
    private String medicineStrength;
}
