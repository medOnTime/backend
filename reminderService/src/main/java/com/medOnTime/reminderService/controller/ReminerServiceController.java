package com.medOnTime.reminderService.controller;

import com.medOnTime.reminderService.dto.ReminderDTO;
import com.medOnTime.reminderService.dto.ReminderSchedulesDTO;
import com.medOnTime.reminderService.service.ReminderScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("reminder")
public class ReminerServiceController {

    @Autowired
    private ReminderScheduleService reminderScheduleService;

    @PostMapping("/addReminder")
    public String addReminders(@RequestBody ReminderDTO reminderDTO) throws Exception {
        return reminderScheduleService.addReminder(reminderDTO);
    }

    @PostMapping("/updateReminder")
    public String updateReminder(@RequestBody ReminderDTO reminderDTO) throws Exception {
        return reminderScheduleService.updateReminder(reminderDTO);
    }

    @PostMapping("/findScheduledRemindersWithFilters/{page}/{pageSize}")
    public Page<ReminderSchedulesDTO> getUserRemindersByFiltering(@RequestBody(required = false) Map<String,String> filter, HttpServletRequest request, @PathVariable int page, @PathVariable int pageSize){
        String userId = request.getHeader("X-User-Id");
        return reminderScheduleService.findScheduledRemindersWithFilters(userId,filter,page,pageSize);
    }

    @GetMapping("/getRemindersByFilterForUpdate")
    public List<ReminderDTO> getRemindersByFilterForUpdate(HttpServletRequest request){
        String userId = request.getHeader("X-User-Id");
        return reminderScheduleService.getRemindersByFilterForUpdate(userId);
    }

    @PostMapping("/scheduleAction")
    public String actionForSchedulers(@RequestBody ReminderSchedulesDTO schedulesDTO){
        return reminderScheduleService.actionForSchedulers(schedulesDTO);
    }
}
