package com.MedOnTime.chatBotService.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface ChatBotServiceRepository {

    List<HashMap<String, String>> findScheduledRemindersWithFilters(
            Integer userId,
            @Nullable String status,
            @Nullable LocalDate date);

    List<HashMap<String, String>> getMedicineInventoryByUser(int userId);

}
