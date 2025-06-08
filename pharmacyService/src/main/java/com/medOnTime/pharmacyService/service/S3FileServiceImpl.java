package com.medOnTime.pharmacyService.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3FileServiceImpl implements S3FileService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public S3FileServiceImpl(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String safeFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String fileName = UUID.randomUUID() + "_" + safeFilename;

        String folderName = "pharmacy-certificates";
        String fullKey = folderName + "/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest,
                software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                        file.getInputStream(), file.getSize()));

        return fullKey;  // store just the S3 key in the DB, not full public URL
    }

    @Override
    public String generatePresignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
//                .responseContentDisposition("attachment; filename=\"" + filename + "\"")
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(15))  // link valid for 15 mins
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
