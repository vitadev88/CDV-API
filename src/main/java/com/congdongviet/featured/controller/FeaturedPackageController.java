package com.congdongviet.featured.controller;

import com.congdongviet.common.dto.ApiResponse;
import com.congdongviet.featured.dto.ApplyPromotionRequest;
import com.congdongviet.featured.dto.FeaturedPackageRequest;
import com.congdongviet.featured.dto.FeaturedPackageResponse;
import com.congdongviet.featured.dto.FeaturedPromotionResponse;
import com.congdongviet.featured.service.FeaturedPackageService;
import com.congdongviet.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/v1/featured-packages")
@RequiredArgsConstructor
public class FeaturedPackageController {

    private final FeaturedPackageService service;

    /** Public — lấy danh sách gói đang hoạt động */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<FeaturedPackageResponse>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(service.getActivePackages(), "OK"));
    }

    /** Admin — lấy tất cả gói */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<FeaturedPackageResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllPackages(), "OK"));
    }

    /** Admin — tạo gói mới */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeaturedPackageResponse>> create(
            @Valid @RequestBody FeaturedPackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.createPackage(request), "Created"));
    }

    /** Admin — cập nhật gói */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeaturedPackageResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody FeaturedPackageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.updatePackage(id, request), "Updated"));
    }

    /** Admin — xoá gói */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.deletePackage(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted"));
    }

    /** User — áp dụng gói cho bài đăng */
    @PostMapping("/apply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FeaturedPromotionResponse>> apply(
            @Valid @RequestBody ApplyPromotionRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.applyPromotion(request, currentUser), "Applied"));
    }

    /** Admin — xem tất cả lịch sử đẩy nổi bật */
    @GetMapping("/promotions")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<FeaturedPromotionResponse>>> getPromotions(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(service.getAllPromotions(pageable), "OK"));
    }
}
