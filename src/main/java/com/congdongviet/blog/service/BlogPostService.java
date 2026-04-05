package com.congdongviet.blog.service;

import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.blog.dto.request.CreateBlogCategoryRequest;
import com.congdongviet.blog.dto.request.CreateBlogPostRequest;
import com.congdongviet.blog.dto.request.UpdateBlogPostRequest;
import com.congdongviet.blog.dto.response.BlogCategoryResponse;
import com.congdongviet.blog.dto.response.BlogPostResponse;
import com.congdongviet.blog.dto.response.BlogPostSummaryResponse;
import com.congdongviet.blog.entity.BlogCategory;
import com.congdongviet.blog.entity.BlogPost;
import com.congdongviet.blog.entity.BlogStatus;
import com.congdongviet.blog.repository.BlogCategoryRepository;
import com.congdongviet.blog.repository.BlogPostRepository;
import com.congdongviet.blog.util.SlugUtils;
import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.common.exception.ResourceNotFoundException;
import com.congdongviet.security.UserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // ── Categories ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<BlogCategoryResponse> getAllCategories() {
        return blogCategoryRepository.findAll(Sort.by("name"))
                .stream().map(BlogCategoryResponse::from).toList();
    }

    @Transactional
    public BlogCategoryResponse createCategory(CreateBlogCategoryRequest req) {
        String slug = SlugUtils.toSlug(req.getName());
        if (blogCategoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Category slug already exists: " + slug);
        }
        BlogCategory cat = BlogCategory.builder()
                .name(req.getName())
                .slug(slug)
                .description(req.getDescription())
                .build();
        return BlogCategoryResponse.from(blogCategoryRepository.save(cat));
    }

    // ── Blog Posts ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<BlogPostSummaryResponse> getPublishedPosts(String keyword, String categoryId,
                                                            Pageable pageable) {
        UUID catId = categoryId != null && !categoryId.isBlank()
                ? UUID.fromString(categoryId) : null;
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        // Force safe sort — avoids Spring Data JPA 3.x JPQL sort expression validation error
        Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "publishedAt"));
        return blogPostRepository.findFiltered(BlogStatus.PUBLISHED, catId, kw, safe)
                .map(BlogPostSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<BlogPostSummaryResponse> getAllPostsAdmin(String keyword, String status,
                                                          Pageable pageable) {
        BlogStatus bs = null;
        if (status != null && !status.isBlank()) {
            try { bs = BlogStatus.valueOf(status.toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return blogPostRepository.findAllFiltered(kw, bs, safe)
                .map(BlogPostSummaryResponse::from);
    }

    @Transactional
    public BlogPostResponse getBySlug(String slug) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "slug", slug));

        if (post.getStatus() == BlogStatus.PUBLISHED) {
            blogPostRepository.incrementViewCount(post.getId());
        }
        return BlogPostResponse.from(post);
    }

    @Transactional(readOnly = true)
    public BlogPostResponse getByIdAdmin(UUID id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));
        return BlogPostResponse.from(post);
    }

    @Transactional(readOnly = true)
    public List<BlogPostSummaryResponse> getRelatedPosts(String slug, int limit) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "slug", slug));
        if (post.getTags() == null || post.getTags().isEmpty()) {
            return List.of();
        }
        try {
            String tagsJson = objectMapper.writeValueAsString(post.getTags());
            return blogPostRepository.findRelated(post.getId().toString(), tagsJson, limit)
                    .stream().map(BlogPostSummaryResponse::from).toList();
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<BlogPostSummaryResponse> getTrending(int limit) {
        return blogPostRepository.findTrending(PageRequest.of(0, limit))
                .stream().map(BlogPostSummaryResponse::from).toList();
    }

    @Transactional
    public BlogPostResponse create(CreateBlogPostRequest req, UserPrincipal currentUser) {
        User author = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        String slug = resolveSlug(req.getSlug(), req.getTitle(), null);
        BlogStatus status = parseStatus(req.getStatus());

        BlogPost post = BlogPost.builder()
                .title(req.getTitle())
                .slug(slug)
                .content(req.getContent())
                .excerpt(buildExcerpt(req.getExcerpt(), req.getContent()))
                .thumbnail(req.getThumbnail())
                .category(resolveCategory(req.getCategoryId()))
                .author(author)
                .tags(req.getTags() != null ? req.getTags() : List.of())
                .metaTitle(req.getMetaTitle())
                .metaDescription(req.getMetaDescription())
                .status(status)
                .publishedAt(status == BlogStatus.PUBLISHED ? LocalDateTime.now() : null)
                .build();

        return BlogPostResponse.from(blogPostRepository.save(post));
    }

    @Transactional
    public BlogPostResponse update(UUID id, UpdateBlogPostRequest req, UserPrincipal currentUser) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));

        if (req.getTitle() != null)     post.setTitle(req.getTitle());
        if (req.getContent() != null)   post.setContent(req.getContent());
        if (req.getThumbnail() != null) post.setThumbnail(req.getThumbnail());
        if (req.getTags() != null)      post.setTags(req.getTags());
        if (req.getMetaTitle() != null) post.setMetaTitle(req.getMetaTitle());
        if (req.getMetaDescription() != null) post.setMetaDescription(req.getMetaDescription());
        if (req.getCategoryId() != null) post.setCategory(resolveCategory(req.getCategoryId()));

        if (req.getExcerpt() != null) {
            post.setExcerpt(req.getExcerpt());
        } else if (req.getContent() != null) {
            post.setExcerpt(buildExcerpt(null, req.getContent()));
        }

        if (req.getSlug() != null && !req.getSlug().isBlank()) {
            String newSlug = resolveSlug(req.getSlug(), null, post.getId().toString());
            post.setSlug(newSlug);
        } else if (req.getTitle() != null) {
            post.setSlug(resolveSlug(null, req.getTitle(), post.getId().toString()));
        }

        if (req.getStatus() != null) {
            BlogStatus newStatus = parseStatus(req.getStatus());
            if (newStatus == BlogStatus.PUBLISHED && post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
            }
            post.setStatus(newStatus);
        }

        return BlogPostResponse.from(blogPostRepository.save(post));
    }

    @Transactional
    public void delete(UUID id) {
        if (!blogPostRepository.existsById(id)) {
            throw new ResourceNotFoundException("BlogPost", "id", id);
        }
        blogPostRepository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String resolveSlug(String requestedSlug, String title, String excludeId) {
        String base = (requestedSlug != null && !requestedSlug.isBlank())
                ? SlugUtils.toSlug(requestedSlug)
                : SlugUtils.toSlug(title);

        if (base.isBlank()) throw new BadRequestException("Cannot generate slug from provided title");

        String candidate = base;
        int suffix = 1;
        while (blogPostRepository.existsBySlug(candidate)) {
            // If updating and the slug belongs to the same post, it's fine
            if (excludeId != null) {
                UUID excludeUUID = UUID.fromString(excludeId);
                boolean ownSlug = blogPostRepository.findBySlug(candidate)
                        .map(p -> p.getId().equals(excludeUUID))
                        .orElse(false);
                if (ownSlug) break;
            }
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    private BlogCategory resolveCategory(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) return null;
        return blogCategoryRepository.findById(UUID.fromString(categoryId))
                .orElseThrow(() -> new ResourceNotFoundException("BlogCategory", "id", categoryId));
    }

    private BlogStatus parseStatus(String status) {
        if (status == null) return BlogStatus.DRAFT;
        try { return BlogStatus.valueOf(status.toUpperCase()); }
        catch (IllegalArgumentException e) { return BlogStatus.DRAFT; }
    }

    private String buildExcerpt(String provided, String content) {
        if (provided != null && !provided.isBlank()) return provided;
        if (content == null) return null;
        // Strip markdown-ish tags, take first 200 chars
        String plain = content.replaceAll("<[^>]+>", "").replaceAll("[#*`>]", "").trim();
        return plain.length() > 200 ? plain.substring(0, 197) + "..." : plain;
    }
}
