package com.congdongviet.blog.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateBlogPostRequest {

    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private String thumbnail;
    private String categoryId;
    private List<String> tags;
    private String metaTitle;
    private String metaDescription;
    private String status;
}
