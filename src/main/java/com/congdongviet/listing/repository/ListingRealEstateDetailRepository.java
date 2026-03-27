package com.congdongviet.listing.repository;

import com.congdongviet.listing.entity.ListingRealEstateDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListingRealEstateDetailRepository extends JpaRepository<ListingRealEstateDetail, UUID> {
}
