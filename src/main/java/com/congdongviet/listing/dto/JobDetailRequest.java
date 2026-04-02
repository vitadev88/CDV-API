package com.congdongviet.listing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class JobDetailRequest {

    /** FULL_TIME | PART_TIME | CONTRACT | FREELANCE */
    private String jobType;

    @NotBlank(message = "City is required for job listing")
    private String city;

    private String district;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private Integer experienceYears;

    private String contactName;
}
