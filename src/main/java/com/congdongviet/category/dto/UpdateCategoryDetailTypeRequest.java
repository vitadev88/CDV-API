package com.congdongviet.category.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateCategoryDetailTypeRequest {

    @Size(max = 50)
    private String code;

    @Size(max = 100)
    private String name;

    private Integer sortOrder;
}
