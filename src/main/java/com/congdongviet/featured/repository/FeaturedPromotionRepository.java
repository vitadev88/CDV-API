package com.congdongviet.featured.repository;

import com.congdongviet.featured.entity.FeaturedPromotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeaturedPromotionRepository extends JpaRepository<FeaturedPromotion, UUID> {
    Page<FeaturedPromotion> findAllByOrderByCreatedAtDesc(Pageable pageable);
    boolean existsByListingIdAndEndAtAfter(UUID listingId, java.time.LocalDateTime now);
}
