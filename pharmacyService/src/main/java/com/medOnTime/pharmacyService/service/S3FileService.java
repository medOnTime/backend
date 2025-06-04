package com.medOnTime.pharmacyService.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3FileService {
    String uploadFile(MultipartFile file) throws IOException;
}
