package com.medOnTime.medicineService.Dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medOnTime.medicineService.dto.MedicineDTO;
import org.junit.Test;

import static org.junit.Assert.*;

public class MedicineDTOTest {

    @Test
    public void testBuilderAndGetters() {
        MedicineDTO dto = MedicineDTO.builder()
                .medicine_id(1)
                .medicine_name("Panadol")
                .description("Painkiller")
                .type("Tablet")
                .strength("500mg")
                .build();

        assertEquals(1, dto.getMedicine_id());
        assertEquals("Panadol", dto.getMedicine_name());
        assertEquals("Painkiller", dto.getDescription());
        assertEquals("Tablet", dto.getType());
        assertEquals("500mg", dto.getStrength());
    }

    @Test
    public void testSerialization() throws Exception {
        MedicineDTO dto = MedicineDTO.builder()
                .medicine_id(1)
                .medicine_name("Panadol")
                .description("Painkiller")
                .type("Tablet")
                .strength("500mg")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"medicine_name\":\"Panadol\""));
        assertTrue(json.contains("\"strength\":\"500mg\""));
    }
}
