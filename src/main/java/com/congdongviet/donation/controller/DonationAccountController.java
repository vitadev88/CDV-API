package com.congdongviet.donation.controller;

import com.congdongviet.common.dto.ApiResponse;
import com.congdongviet.donation.dto.DonationAccountRequest;
import com.congdongviet.donation.dto.DonationAccountResponse;
import com.congdongviet.donation.service.DonationAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/donation-accounts")
@RequiredArgsConstructor
public class DonationAccountController {

    private final DonationAccountService service;

    /** Public — hiển thị banner donate */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DonationAccountResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(service.getActive(), "OK"));
    }

    /** Super Admin — xem tất cả */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<DonationAccountResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(), "OK"));
    }

    /** Super Admin — tạo mới */
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DonationAccountResponse>> create(
            @Valid @RequestBody DonationAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Created"));
    }

    /** Super Admin — cập nhật */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DonationAccountResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody DonationAccountRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Updated"));
    }

    /** Super Admin — xoá */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted"));
    }
}
