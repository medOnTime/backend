package com.medOnTime.reminderService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReminderDTO {

    private String reminderId;
    private String userId;
    private String medicineId;
    private String medicineName;
    private String medicineType;
    private String strength;
    private Integer timesPerDay;
    private List<Integer> dosageList;
    private String dosageString;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    private Integer numberOfDays;
}

