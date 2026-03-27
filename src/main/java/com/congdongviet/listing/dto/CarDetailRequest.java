package com.congdongviet.listing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarDetailRequest {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Manufacture year is required")
    private Integer manufactureYear;

    private Integer mileageKm;
    private String fuelType;
    private String transmission;
    private String color;
}
