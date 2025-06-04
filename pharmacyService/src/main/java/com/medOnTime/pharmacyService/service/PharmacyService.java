package com.medOnTime.pharmacyService.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

public interface PharmacyService {

    String addPharmacy(String name, String address, String contactNumber,
                               String certificateNumber, String certifiedDate,
                               MultipartFile certificateFile) throws IOException;

}
