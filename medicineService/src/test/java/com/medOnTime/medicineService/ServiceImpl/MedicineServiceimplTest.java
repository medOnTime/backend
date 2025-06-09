package com.medOnTime.medicineService.ServiceImpl;

import com.medOnTime.medicineService.dto.MedicineDTO;
import com.medOnTime.medicineService.repo.MedicineRepository;
import com.medOnTime.medicineService.service.MedicineServiceimpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MedicineServiceimplTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineServiceimpl medicineService;

    private MedicineDTO testMedicineDTO;
    private HashMap<String, String> testMedicineInventory;
    private HashMap<String, String> testAddedMedicine;

    @Before
    public void setUp() {
        // Using builder pattern from Lombok
        testMedicineDTO = MedicineDTO.builder()
                .medicine_id(1)
                .medicine_name("Test Medicine")
                .description("Test Description")
                .type("Tablet")
                .strength("500mg")
                .build();

        testMedicineInventory = new HashMap<>();
        testMedicineInventory.put("inventoryId", "1");
        testMedicineInventory.put("medicineId", "1");
        testMedicineInventory.put("quantity", "10");
        testMedicineInventory.put("startDate", "2023-01-01");
        testMedicineInventory.put("endDate", "2023-12-31");

        testAddedMedicine = new HashMap<>();
        testAddedMedicine.put("userId", "1");
        testAddedMedicine.put("medicineId", "1");
        testAddedMedicine.put("quantity", "5");
        testAddedMedicine.put("startDate", "2023-01-01");
        testAddedMedicine.put("endDate", "2023-12-31");
    }

    @Test
    public void testGetMedicineDetailById() {
        when(medicineRepository.getMedicineDetailById(1)).thenReturn(testMedicineDTO);

        MedicineDTO result = medicineService.getMedicineDetailById("1");

        assertNotNull(result);
        assertEquals(1, result.getMedicine_id());
        assertEquals("Test Medicine", result.getMedicine_name());
        verify(medicineRepository, times(1)).getMedicineDetailById(1);
    }

    @Test
    public void testGetAllMedicines() {
        List<MedicineDTO> expectedList = Arrays.asList(testMedicineDTO);
        when(medicineRepository.getAllMedicines()).thenReturn(expectedList);

        List<MedicineDTO> result = medicineService.getAllMedicines();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMedicineDTO, result.get(0));
        verify(medicineRepository, times(1)).getAllMedicines();
    }

    @Test
    public void testAddMedicineToInventry_NewInventory() throws Exception {
        when(medicineRepository.getMedicineInventoryByUserIdAndMedicineID(1, 1))
                .thenReturn(new HashMap<>());
        when(medicineRepository.addMedicineToInventry(testAddedMedicine))
                .thenReturn("Success");

        String result = medicineService.addMedicineToInventry(testAddedMedicine);

        assertEquals("Success", result);
        verify(medicineRepository, times(1)).getMedicineInventoryByUserIdAndMedicineID(1, 1);
        verify(medicineRepository, times(1)).addMedicineToInventry(testAddedMedicine);
        verify(medicineRepository, never()).updateMedicineInventory(any());
    }

    @Test
    public void testAddMedicineToInventry_ExistingInventory() throws Exception {
        when(medicineRepository.getMedicineInventoryByUserIdAndMedicineID(1, 1))
                .thenReturn(testMedicineInventory);
        when(medicineRepository.updateMedicineInventory(any()))
                .thenReturn("Updated");

        String result = medicineService.addMedicineToInventry(testAddedMedicine);

        assertEquals("Updated", result);
        verify(medicineRepository, times(1)).getMedicineInventoryByUserIdAndMedicineID(1, 1);
        verify(medicineRepository, never()).addMedicineToInventry(any());
        verify(medicineRepository, times(1)).updateMedicineInventory(any());

        // Verify the updated quantity (10 existing + 5 added = 15)
        HashMap<String, String> expectedUpdate = new HashMap<>();
        expectedUpdate.put("quantity", "15");
        expectedUpdate.put("startDate", "2023-01-01");
        expectedUpdate.put("endDate", "2023-12-31");
        expectedUpdate.put("inventoryId", "1");
    }

    @Test
    public void testGetMedicineInventoryByUser() {
        List<HashMap<String, String>> expectedList = Arrays.asList(testMedicineInventory);
        when(medicineRepository.getMedicineInventoryByUser(1))
                .thenReturn(expectedList);

        List<HashMap<String, String>> result = medicineService.getMedicineInventoryByUser("1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMedicineInventory, result.get(0));
        verify(medicineRepository, times(1)).getMedicineInventoryByUser(1);
    }

    @Test
    public void testUpdateInventory() {
        when(medicineRepository.updateMedicineInventory(testMedicineInventory))
                .thenReturn("Update Success");

        String result = medicineService.updateInventory(testMedicineInventory);

        assertEquals("Update Success", result);
        verify(medicineRepository, times(1)).updateMedicineInventory(testMedicineInventory);
    }

    @Test(expected = Exception.class)
    public void testAddMedicineToInventry_Exception() throws Exception {
        when(medicineRepository.getMedicineInventoryByUserIdAndMedicineID(1, 1))
                .thenThrow(new RuntimeException("DB Error"));

        medicineService.addMedicineToInventry(testAddedMedicine);
    }
}