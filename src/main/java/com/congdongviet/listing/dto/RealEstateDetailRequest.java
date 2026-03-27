package com.congdongviet.listing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RealEstateDetailRequest {

    @NotBlank(message = "City is required")
    private String city;

    private String district;
    private String address;
    private BigDecimal areaSqm;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floors;
    private boolean furnished;

    /** Chỉ cần điền nếu là cho thuê */
    private LocalDate availableFrom;
    private BigDecimal deposit;
}
