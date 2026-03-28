package com.congdongviet.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateCategoryRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    /** Null = danh mục gốc */
    private UUID parentId;

    /** REAL_ESTATE | CAR | null */
    private String detailType;

    private int sortOrder = 0;
}
