package com.congdongviet.listing.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoodsDetailResponse {

    private String condition;
    private String brand;
    private String city;
}
