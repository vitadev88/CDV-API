package com.congdongviet.blog.dto.response;

import com.congdongviet.blog.entity.BlogCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BlogCategoryResponse {

    private String id;
    private String name;
    private String slug;
    private String description;
    private LocalDateTime createdAt;

    public static BlogCategoryResponse from(BlogCategory c) {
        return BlogCategoryResponse.builder()
                .id(c.getId().toString())
                .name(c.getName())
                .slug(c.getSlug())
                .description(c.getDescription())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
