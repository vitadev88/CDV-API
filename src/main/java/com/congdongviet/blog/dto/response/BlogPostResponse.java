package com.congdongviet.blog.dto.response;

import com.congdongviet.blog.entity.BlogPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BlogPostResponse {

    private String id;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private String thumbnail;
    private String categoryId;
    private String categoryName;
    private String categorySlug;
    private String authorId;
    private String authorName;
    private String authorAvatarUrl;
    private List<String> tags;
    private String metaTitle;
    private String metaDescription;
    private String status;
    private Long viewCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BlogPostResponse from(BlogPost p) {
        return BlogPostResponse.builder()
                .id(p.getId().toString())
                .title(p.getTitle())
                .slug(p.getSlug())
                .content(p.getContent())
                .excerpt(p.getExcerpt())
                .thumbnail(p.getThumbnail())
                .categoryId(p.getCategory() != null ? p.getCategory().getId().toString() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .categorySlug(p.getCategory() != null ? p.getCategory().getSlug() : null)
                .authorId(p.getAuthor().getId().toString())
                .authorName(p.getAuthor().getFullName())
                .authorAvatarUrl(p.getAuthor().getAvatarUrl())
                .tags(p.getTags())
                .metaTitle(p.getMetaTitle())
                .metaDescription(p.getMetaDescription())
                .status(p.getStatus().name())
                .viewCount(p.getViewCount())
                .publishedAt(p.getPublishedAt())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
