package com.congdongviet.blog.controller;

import com.congdongviet.blog.dto.request.CreateBlogCategoryRequest;
import com.congdongviet.blog.dto.request.CreateBlogPostRequest;
import com.congdongviet.blog.dto.request.UpdateBlogPostRequest;
import com.congdongviet.blog.dto.response.BlogCategoryResponse;
import com.congdongviet.blog.dto.response.BlogPostResponse;
import com.congdongviet.blog.dto.response.BlogPostSummaryResponse;
import com.congdongviet.blog.service.BlogPostService;
import com.congdongviet.common.dto.ApiResponse;
import com.congdongviet.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/blog")
@RequiredArgsConstructor
public class BlogPostController {

    private final BlogPostService blogPostService;

    // ── Public endpoints ──────────────────────────────────────────────────────

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<BlogCategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(blogPostService.getAllCategories()));
    }

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<BlogPostSummaryResponse>>> getPublishedPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @PageableDefault(size = 10, sort = "publishedAt,desc") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                blogPostService.getPublishedPosts(keyword, categoryId, pageable)));
    }

    @GetMapping("/posts/{slug}")
    public ResponseEntity<ApiResponse<BlogPostResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(blogPostService.getBySlug(slug)));
    }

    @GetMapping("/posts/{slug}/related")
    public ResponseEntity<ApiResponse<List<BlogPostSummaryResponse>>> getRelated(
            @PathVariable String slug,
            @RequestParam(defaultValue = "4") int limit) {
        return ResponseEntity.ok(ApiResponse.success(blogPostService.getRelatedPosts(slug, limit)));
    }

    @GetMapping("/posts/trending")
    public ResponseEntity<ApiResponse<List<BlogPostSummaryResponse>>> getTrending(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success(blogPostService.getTrending(limit)));
    }

    // ── Admin endpoints ───────────────────────────────────────────────────────

    @GetMapping("/admin/posts")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BlogPostSummaryResponse>>> adminGetAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt,desc") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                blogPostService.getAllPostsAdmin(keyword, status, pageable)));
    }

    @GetMapping("/admin/posts/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BlogPostResponse>> adminGetById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(blogPostService.getByIdAdmin(id)));
    }

    @PostMapping("/admin/categories")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BlogCategoryResponse>> createCategory(
            @Valid @RequestBody CreateBlogCategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(blogPostService.createCategory(req), "Category created"));
    }

    @PostMapping("/admin/posts")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BlogPostResponse>> create(
            @Valid @RequestBody CreateBlogPostRequest req,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(blogPostService.create(req, currentUser), "Post created"));
    }

    @PatchMapping("/admin/posts/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BlogPostResponse>> update(
            @PathVariable UUID id,
            @RequestBody UpdateBlogPostRequest req,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                blogPostService.update(id, req, currentUser), "Post updated"));
    }

    @DeleteMapping("/admin/posts/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        blogPostService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Post deleted"));
    }
}
