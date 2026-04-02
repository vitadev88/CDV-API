package com.congdongviet.featured.repository;

import com.congdongviet.featured.entity.FeaturedPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeaturedPackageRepository extends JpaRepository<FeaturedPackage, UUID> {
    List<FeaturedPackage> findByActiveTrueOrderBySortOrderAsc();
}
