package com.congdongviet.listing.repository;

import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.entity.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID>, JpaSpecificationExecutor<Listing> {

    @EntityGraph(attributePaths = {"category", "category.detailType", "createdBy", "images"})
    Page<Listing> findByCategoryIdAndStatus(UUID categoryId, ListingStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "category.detailType", "createdBy", "images"})
    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "category.detailType", "createdBy", "images"})
    Page<Listing> findByCreatedByIdAndStatus(UUID userId, ListingStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "category.detailType", "createdBy", "images"})
    Page<Listing> findByFeaturedAndStatus(boolean featured, ListingStatus status, Pageable pageable);

    long countByFeaturedTrue();

    boolean existsByCategoryId(UUID categoryId);

    /** [year, month, count] — số tin đăng theo tháng kể từ :since */
    @Query("SELECT FUNCTION('YEAR', l.createdAt), FUNCTION('MONTH', l.createdAt), COUNT(l) " +
           "FROM Listing l WHERE l.createdAt >= :since " +
           "GROUP BY FUNCTION('YEAR', l.createdAt), FUNCTION('MONTH', l.createdAt) " +
           "ORDER BY FUNCTION('YEAR', l.createdAt), FUNCTION('MONTH', l.createdAt)")
    List<Object[]> countByMonth(@Param("since") LocalDateTime since);

    /** [categoryName, count] — số tin theo danh mục */
    @Query("SELECT l.category.name, COUNT(l) FROM Listing l " +
           "GROUP BY l.category.id, l.category.name ORDER BY COUNT(l) DESC")
    List<Object[]> countByCategory(Pageable pageable);
}
