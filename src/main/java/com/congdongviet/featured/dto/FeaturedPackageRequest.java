package com.congdongviet.featured.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeaturedPackageRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Min(0)
    private long price;

    @Min(1)
    private int durationDays = 7;

    private boolean active = false;

    private int sortOrder = 0;
}
