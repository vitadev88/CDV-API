package com.congdongviet.listing.repository;

import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.entity.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID>, JpaSpecificationExecutor<Listing> {

    Page<Listing> findByCategoryIdAndStatus(UUID categoryId, ListingStatus status, Pageable pageable);

    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    Page<Listing> findByCreatedByIdAndStatus(UUID userId, ListingStatus status, Pageable pageable);

    Page<Listing> findByFeaturedAndStatus(boolean featured, ListingStatus status, Pageable pageable);
}
