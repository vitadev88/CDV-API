package com.congdongviet.category.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateCategoryRequest {

    @Size(max = 100)
    private String name;

    /** Null = giữ nguyên parent hiện tại */
    private UUID parentId;

    /** REAL_ESTATE | CAR | null */
    private String detailType;

    private Boolean active;

    private Integer sortOrder;
}
