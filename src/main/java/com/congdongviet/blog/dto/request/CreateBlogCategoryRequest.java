package com.congdongviet.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBlogCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
}
