package com.congdongviet.listing.repository;

import com.congdongviet.listing.entity.ListingJobDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListingJobDetailRepository extends JpaRepository<ListingJobDetail, UUID> {
}
