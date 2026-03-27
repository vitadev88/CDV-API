package com.congdongviet.listing.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarDetailResponse {

    private String brand;
    private String model;
    private Integer manufactureYear;
    private Integer mileageKm;
    private String fuelType;
    private String transmission;
    private String color;
}
