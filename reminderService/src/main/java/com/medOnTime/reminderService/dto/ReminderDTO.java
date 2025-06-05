package com.medOnTime.reminderService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReminderDTO {

    private String reminderId;
    private String userId;
    private String medicineId;
    private String medicineName;
    private String medicineType;
    private String dosage;
    private Integer hours;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    private Integer numberOfDays;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
}

