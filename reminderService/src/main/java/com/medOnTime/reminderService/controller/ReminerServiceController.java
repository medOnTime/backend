package com.medOnTime.reminderService.controller;

import com.medOnTime.reminderService.dto.ReminderDTO;
import com.medOnTime.reminderService.service.ReminderScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reminder")
public class ReminerServiceController {

    @Autowired
    private ReminderScheduleService reminderScheduleService;

    @PostMapping("/addReminder")
    public String addReminders(@RequestBody ReminderDTO reminderDTO) throws Exception {
        return reminderScheduleService.addReminder(reminderDTO);
    }

}
