package com.congdongviet.featured.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FeaturedPackageResponse {
    private UUID id;
    private String name;
    private long price;
    private int durationDays;
    private boolean active;
    private int sortOrder;
}
