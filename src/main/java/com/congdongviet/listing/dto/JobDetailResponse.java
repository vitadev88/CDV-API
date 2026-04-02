package com.congdongviet.listing.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class JobDetailResponse {

    private String jobType;
    private String city;
    private String district;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private Integer experienceYears;
    private String contactName;
}
