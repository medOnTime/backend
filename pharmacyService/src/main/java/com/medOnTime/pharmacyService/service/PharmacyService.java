package com.medOnTime.pharmacyService.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface PharmacyService {

    String addPharmacy(String pharmacyName,String address, String contactNumber, String licenseNumber, MultipartFile licence) throws IOException;
}
