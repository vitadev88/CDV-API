package com.congdongviet.category.service;

import com.congdongviet.category.dto.CategoryResponse;
import com.congdongviet.category.dto.CreateCategoryRequest;
import com.congdongviet.category.dto.UpdateCategoryRequest;
import com.congdongviet.category.entity.Category;
import com.congdongviet.category.entity.CategoryDetailType;
import com.congdongviet.category.repository.CategoryRepository;
import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /** Public: trả về cây danh mục đang active */
    public List<CategoryResponse> getTree() {
        return categoryRepository.findByParentIsNullAndActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /** Admin: lấy 1 category theo id (kể cả inactive) */
    @Transactional(readOnly = true)
    public CategoryResponse getById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return toResponse(category);
    }

    /** Admin: tạo mới category */
    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        if (categoryRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Category code already exists: " + request.getCode());
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
        }

        CategoryDetailType detailType = parseDetailType(request.getDetailType());

        Category category = Category.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .parent(parent)
                .detailType(detailType)
                .active(true)
                .sortOrder(request.getSortOrder())
                .build();

        return toResponse(categoryRepository.save(category));
    }

    /** Admin: cập nhật category */
    @Transactional
    public CategoryResponse update(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (request.getName() != null) {
            category.setName(request.getName());
        }

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BadRequestException("Category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
            category.setParent(parent);
        }

        if (request.getDetailType() != null) {
            category.setDetailType(parseDetailType(request.getDetailType()));
        }

        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }

        return toResponse(categoryRepository.save(category));
    }

    /** Admin: xoá mềm (set active = false) */
    @Transactional
    public void delete(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (categoryRepository.existsByParentId(id)) {
            throw new BadRequestException("Cannot delete category that has child categories");
        }

        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryDetailType parseDetailType(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return CategoryDetailType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid detailType: " + value);
        }
    }

    private CategoryResponse toResponse(Category category) {
        List<CategoryResponse> children = categoryRepository
                .findByParentIdAndActiveTrue(category.getId())
                .stream()
                .map(this::toResponse)
                .toList();

        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .detailType(category.getDetailType() != null ? category.getDetailType().name() : null)
                .active(category.isActive())
                .sortOrder(category.getSortOrder())
                .children(children)
                .build();
    }
}
