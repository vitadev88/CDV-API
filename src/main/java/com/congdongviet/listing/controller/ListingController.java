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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse<Page<ListingResponse>>> getByCategory(
            @RequestParam String categoryCode,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.success(listingService.getByCategoryCode(categoryCode, pageable), "OK"));
    }

    /** Chủ tin cập nhật trạng thái: INACTIVE, SOLD, RENTED */
    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable UUID id,
            @RequestParam ListingStatus status,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        listingService.updateStatus(id, status, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null, "Status updated"));
    }
}
