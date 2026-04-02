package com.congdongviet.category.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CategoryResponse {

    private UUID id;
    private String code;
    private String name;
    private UUID detailTypeId;
    private String detailType;     // code, e.g. "REAL_ESTATE"
    private String detailTypeName; // label, e.g. "Bất động sản"
    private boolean active;
    private int sortOrder;
    private List<CategoryResponse> children;
}
