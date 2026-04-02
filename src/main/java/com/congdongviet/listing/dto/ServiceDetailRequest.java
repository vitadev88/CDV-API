package com.congdongviet.listing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDetailRequest {

    @NotBlank(message = "City is required for service listing")
    private String city;

    private String district;

    private String contactName;

    private String contactPhone;
}
