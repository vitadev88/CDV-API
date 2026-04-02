package com.congdongviet.featured.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FeaturedPromotionResponse {
    private UUID id;
    private UUID listingId;
    private String listingTitle;
    private UUID packageId;
    private String packageName;
    private UUID userId;
    private String userName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
}
