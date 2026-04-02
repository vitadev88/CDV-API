package com.congdongviet.listing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsDetailRequest {

    /** NEW | LIKE_NEW | GOOD | FAIR */
    private String condition;

    private String brand;

    private String city;
}
