package com.congdongviet.listing.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class RealEstateDetailResponse {

    private String city;
    private String district;
    private String address;
    private BigDecimal areaSqm;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floors;
    private boolean furnished;
    private LocalDate availableFrom;
    private BigDecimal deposit;
}
