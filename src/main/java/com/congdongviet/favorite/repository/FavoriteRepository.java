package com.congdongviet.favorite.repository;

import com.congdongviet.favorite.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    boolean existsByUserIdAndListingId(UUID userId, UUID listingId);

    Optional<Favorite> findByUserIdAndListingId(UUID userId, UUID listingId);

    @EntityGraph(attributePaths = {"listing", "listing.category", "listing.createdBy", "listing.images"})
    Page<Favorite> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<Favorite> findByUserIdAndListingIdIn(UUID userId, List<UUID> listingIds);

    void deleteByUserIdAndListingId(UUID userId, UUID listingId);
}
