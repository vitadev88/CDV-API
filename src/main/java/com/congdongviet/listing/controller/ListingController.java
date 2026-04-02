package com.congdongviet.listing.controller;

import com.congdongviet.common.dto.ApiResponse;
import com.congdongviet.listing.dto.CreateListingRequest;
import com.congdongviet.listing.dto.ListingResponse;
import com.congdongviet.listing.entity.ListingStatus;
import com.congdongviet.listing.service.ListingService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ListingResponse>> create(
            @Valid @RequestBody CreateListingRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        ListingResponse data = listingService.create(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "Listing created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ListingResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(listingService.getById(id), "OK"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ListingResponse>>> getListings(
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minArea,
            @RequestParam(required = false) Double maxArea,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        if (Boolean.TRUE.equals(featured)) {
            return ResponseEntity.ok(ApiResponse.success(listingService.getFeatured(pageable), "OK"));
        }
        return ResponseEntity.ok(ApiResponse.success(
                listingService.search(categoryCode, keyword, minPrice, maxPrice, city, minArea, maxArea, pageable), "OK"));
    }

    /** Lấy danh sách tin đăng của người dùng hiện tại */
    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ListingResponse>>> getMyListings(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                listingService.getMyListings(currentUser.getId(), pageable), "OK"));
    }

    /** Chủ tin cập nhật trạng thái: INACTIVE, SOLD, RENTED */
    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        ListingStatus status = ListingStatus.valueOf(body.get("status"));
        listingService.updateStatus(id, status, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Status updated"));
    }

    /** Admin: bật/tắt nổi bật cho bài đăng */
    @PatchMapping("/{id}/featured")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> setFeatured(
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> body) {

        boolean featured = Boolean.TRUE.equals(body.get("featured"));
        listingService.setFeatured(id, featured);
        return ResponseEntity.ok(ApiResponse.success(null, "Featured status updated"));
    }

    /** Admin: lấy tất cả listing mọi trạng thái */
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<ListingResponse>>> getAllAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) Boolean featured,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                listingService.getAllAdmin(keyword, status, categoryCode, featured, pageable), "OK"));
    }

    /** Admin: xoá cứng listing */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        listingService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Listing deleted"));
    }
}
