package com.congdongviet.blog.repository;

import com.congdongviet.blog.entity.BlogPost;
import com.congdongviet.blog.entity.BlogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {

    @EntityGraph(attributePaths = {"author", "category"})
    Page<BlogPost> findByStatus(BlogStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "category"})
    Page<BlogPost> findByStatusAndCategoryId(BlogStatus status, UUID categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "category"})
    @Query("SELECT p FROM BlogPost p WHERE p.status = :status AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<BlogPost> searchByKeyword(@Param("status") BlogStatus status,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);

    @EntityGraph(attributePaths = {"author", "category"})
    @Query("SELECT p FROM BlogPost p WHERE p.status = :status AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<BlogPost> findFiltered(@Param("status") BlogStatus status,
                                @Param("categoryId") UUID categoryId,
                                @Param("keyword") String keyword,
                                Pageable pageable);

    @EntityGraph(attributePaths = {"author", "category"})
    Optional<BlogPost> findBySlug(String slug);

    boolean existsBySlug(String slug);

    // Related posts by shared tags — native query for JSON_OVERLAPS (MySQL 8+)
    @Query(value = "SELECT * FROM blog_posts p " +
                   "WHERE p.status = 'PUBLISHED' AND p.id <> :excludeId " +
                   "AND JSON_OVERLAPS(p.tags, :tags) = TRUE " +
                   "ORDER BY p.published_at DESC LIMIT :lim",
           nativeQuery = true)
    List<BlogPost> findRelated(@Param("excludeId") String excludeId,
                               @Param("tags") String tagsJson,
                               @Param("lim") int limit);

    @EntityGraph(attributePaths = {"author", "category"})
    @Query("SELECT p FROM BlogPost p WHERE p.status = 'PUBLISHED' ORDER BY p.viewCount DESC")
    List<BlogPost> findTrending(Pageable pageable);

    @Modifying
    @Query("UPDATE BlogPost p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") UUID id);

    /** Finds SCHEDULED posts whose scheduledAt has passed — used by BlogScheduler. */
    @EntityGraph(attributePaths = {"author", "category"})
    @Query("SELECT p FROM BlogPost p WHERE p.status = :status AND p.scheduledAt <= :now")
    List<BlogPost> findScheduledDue(@Param("status") BlogStatus status,
                                    @Param("now") LocalDateTime now);

    // Admin: all statuses
    @EntityGraph(attributePaths = {"author", "category"})
    @Query("SELECT p FROM BlogPost p WHERE " +
           "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<BlogPost> findAllFiltered(@Param("keyword") String keyword,
                                   @Param("status") BlogStatus status,
                                   Pageable pageable);
}
