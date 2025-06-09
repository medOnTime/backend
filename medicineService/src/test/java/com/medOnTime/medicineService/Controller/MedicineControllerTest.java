package com.medOnTime.medicineService.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medOnTime.medicineService.controller.medicineController;
import com.medOnTime.medicineService.dto.MedicineDTO;
import com.medOnTime.medicineService.service.MedicineService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(medicineController.class)
public class MedicineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicineService medicineService;

    private MedicineDTO sampleMedicine;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        sampleMedicine = MedicineDTO.builder()
                .medicine_id(1)
                .medicine_name("Panadol")
                .description("Painkiller")
                .type("Tablet")
                .strength("500mg")
                .build();
    }

    @Test
    public void testGetMedicineById() throws Exception {
        when(medicineService.getMedicineDetailById("med001")).thenReturn(sampleMedicine);

        mockMvc.perform(get("/medicine/getMedicineById")
                        .param("medicineId", "med001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicine_name").value("Panadol"));
    }

    @Test
    public void testGetAllMedicines() throws Exception {
        List<MedicineDTO> medicineList = Collections.singletonList(sampleMedicine);
        when(medicineService.getAllMedicines()).thenReturn(medicineList);

        mockMvc.perform(get("/medicine/getAllMedicines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicine_name").value("Panadol"));
    }

    @Test
    public void testAddMedicineToInventry() throws Exception {
        Map<String, String> medicineDetails = new HashMap<>();
        medicineDetails.put("medicine_name", "Panadol");

        when(medicineService.addMedicineToInventry(any())).thenReturn("Medicine added");

        mockMvc.perform(post("/medicine/addMedicineToInventry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicineDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Medicine added"));
    }

    @Test
    public void testGetMedicineInventoryByUser() throws Exception {
        List<HashMap<String, String>> mockInventory = new ArrayList<>();
        HashMap<String, String> entry = new HashMap<>();
        entry.put("medicine_name", "Panadol");
        mockInventory.add(entry);

        when(medicineService.getMedicineInventoryByUser("user123")).thenReturn(mockInventory);

        mockMvc.perform(get("/medicine/getMedicineInventoryByUser")
                        .header("X-User-Id", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicine_name").value("Panadol"));
    }

    @Test
    public void testUpdateInventory() throws Exception {
        Map<String, String> updateData = new HashMap<>();
        updateData.put("medicine_id", "1");

        when(medicineService.updateInventory(any())).thenReturn("Inventory updated");

        mockMvc.perform(post("/medicine/updateInventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventory updated"));
    }
}
