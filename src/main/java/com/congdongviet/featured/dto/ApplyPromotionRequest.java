package com.congdongviet.featured.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ApplyPromotionRequest {

    @NotNull
    private UUID listingId;

    @NotNull
    private UUID packageId;
}
