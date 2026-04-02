package com.congdongviet.listing.repository;

import com.congdongviet.listing.entity.ListingGoodsDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListingGoodsDetailRepository extends JpaRepository<ListingGoodsDetail, UUID> {
}
