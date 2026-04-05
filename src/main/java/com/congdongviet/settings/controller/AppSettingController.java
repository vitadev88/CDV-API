package com.congdongviet.settings.controller;

import com.congdongviet.common.dto.ApiResponse;
import com.congdongviet.settings.dto.AppSettingDto;
import com.congdongviet.settings.service.AppSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class AppSettingController {

    private final AppSettingService service;

    @GetMapping
    public ResponseEntity<ApiResponse<AppSettingDto>> get() {
        return ResponseEntity.ok(ApiResponse.success(service.get()));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AppSettingDto>> update(@RequestBody AppSettingDto dto) {
        return ResponseEntity.ok(ApiResponse.success(service.update(dto), "Settings updated"));
    }
}
