package com.congdongviet.category.repository;

import com.congdongviet.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    List<Category> findByParentIsNullAndActiveTrue();

    List<Category> findByParentIdAndActiveTrue(UUID parentId);

    boolean existsByParentId(UUID parentId);
}
