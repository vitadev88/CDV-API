package com.congdongviet.upload.controller;

import com.congdongviet.common.dto.ApiResponse;
import com.congdongviet.upload.dto.UploadResponse;
import com.congdongviet.upload.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final FileStorageService fileStorageService;

    /**
     * Upload one or more image files.
     * @param files    list of multipart files
     * @param folder   S3 folder prefix (e.g. "listings", "avatars")
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UploadResponse>> upload(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {

        List<String> urls = fileStorageService.uploadMultiple(files, folder);
        UploadResponse response = UploadResponse.builder().urls(urls).build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Files uploaded successfully"));
    }

    /**
     * Delete a file by its URL.
     */
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam("url") String url) {
        fileStorageService.delete(url);
        return ResponseEntity.ok(ApiResponse.success(null, "File deleted"));
    }
}
