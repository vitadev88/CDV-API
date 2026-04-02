package com.congdongviet.listing.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceDetailResponse {

    private String city;
    private String district;
    private String contactName;
    private String contactPhone;
}
