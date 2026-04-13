package com.congdongviet.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateBlogPostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    /** If null/blank, auto-generated from title */
    private String slug;

    @NotBlank(message = "Content is required")
    private String content;

    private String excerpt;
    private String thumbnail;
    private String categoryId;
    private List<String> tags;
    private String metaTitle;
    private String metaDescription;

    /** DRAFT, SCHEDULED, or PUBLISHED */
    private String status;

    /** ISO-8601 local datetime (e.g. "2026-04-15T09:00"). Required when status=SCHEDULED. */
    private String scheduledAt;
}
