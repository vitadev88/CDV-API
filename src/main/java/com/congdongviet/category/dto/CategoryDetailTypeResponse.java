package com.congdongviet.category.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CategoryDetailTypeResponse {
    private UUID id;
    private String code;
    private String name;
    private int sortOrder;
}
