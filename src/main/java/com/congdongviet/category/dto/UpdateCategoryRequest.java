package com.congdongviet.category.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateCategoryRequest {

    @Size(max = 50)
    private String code;

    @Size(max = 100)
    private String name;

    /** Null = giữ nguyên parent, empty string = xoá parent (set root) */
    private UUID parentId;

    private Boolean removeParent;

    /** REAL_ESTATE | CAR | null */
    private String detailType;

    private Boolean active;

    private Integer sortOrder;
}
