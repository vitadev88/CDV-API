package com.congdongviet.upload.service;

import com.congdongviet.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    /**
     * Upload a single file to S3.
     * @return the public URL of the uploaded file
     */
    public String upload(MultipartFile file, String folder) {
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String key = folder + "/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to upload file");
        }
    }

    /**
     * Upload multiple files.
     * @return list of public URLs
     */
    public List<String> uploadMultiple(List<MultipartFile> files, String folder) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("No files provided");
        }
        if (files.size() > 10) {
            throw new BadRequestException("Maximum 10 files allowed per upload");
        }
        return files.stream()
                .map(file -> upload(file, folder))
                .toList();
    }

    /**
     * Delete a file from S3 by its full URL.
     */
    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        if (key == null) return;

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to delete file from S3: {}", e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds 20MB limit");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only JPEG, PNG, WebP, and GIF images are allowed");
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }

    private String extractKeyFromUrl(String url) {
        if (url == null) return null;
        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucket, region);
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        return null;
    }
}
