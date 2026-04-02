package com.congdongviet.favorite.controller;

import com.congdongviet.common.dto.ApiResponse;
import com.congdongviet.favorite.service.FavoriteService;
import com.congdongviet.listing.dto.ListingResponse;
import com.congdongviet.listing.service.ListingService;
import com.congdongviet.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final ListingService listingService;

    /** Toggle yêu thích cho 1 listing */
    @PostMapping("/{listingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggle(
            @PathVariable UUID listingId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        boolean isFav = favoriteService.toggle(currentUser.getId(), listingId);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("favorited", isFav),
                isFav ? "Added to favorites" : "Removed from favorites"));
    }

    /** Kiểm tra đã yêu thích chưa */
    @GetMapping("/check/{listingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> check(
            @PathVariable UUID listingId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        boolean isFav = favoriteService.isFavorited(currentUser.getId(), listingId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("favorited", isFav), "OK"));
    }

    /** Danh sách yêu thích → trả ListingResponse */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ListingResponse>>> getMyFavorites(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ListingResponse> page = favoriteService.getMyFavorites(currentUser.getId(), pageable)
                .map(fav -> listingService.getById(fav.getListing().getId()));
        return ResponseEntity.ok(ApiResponse.success(page, "OK"));
    }
}
