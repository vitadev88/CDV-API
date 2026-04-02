package com.congdongviet.category.repository;

import com.congdongviet.category.entity.CategoryDetailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryDetailTypeRepository extends JpaRepository<CategoryDetailType, UUID> {

    Optional<CategoryDetailType> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);
}
