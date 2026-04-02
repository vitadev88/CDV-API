package com.congdongviet.category.service;

import com.congdongviet.category.dto.CategoryResponse;
import com.congdongviet.category.dto.CreateCategoryRequest;
import com.congdongviet.category.dto.UpdateCategoryRequest;
import com.congdongviet.category.entity.Category;
import com.congdongviet.category.entity.CategoryDetailType;
import com.congdongviet.category.repository.CategoryDetailTypeRepository;
import com.congdongviet.category.repository.CategoryRepository;
import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.common.exception.ResourceNotFoundException;
import com.congdongviet.listing.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDetailTypeRepository detailTypeRepository;
    private final ListingRepository listingRepository;

    /** Public: trả về cây danh mục đang active */
    public List<CategoryResponse> getTree() {
        return categoryRepository.findByParentIsNullAndActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /** Admin: trả về toàn bộ cây danh mục (kể cả inactive) */
    public List<CategoryResponse> getAllTree() {
        return categoryRepository.findByParentIsNullOrderBySortOrderAsc()
                .stream()
                .map(this::toResponseAll)
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

        CategoryDetailType detailType = resolveDetailType(request.getDetailType());

        Category category = Category.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .parent(parent)
                .detailType(detailType)
                .active(false)
                .sortOrder(request.getSortOrder())
                .build();

        return toResponse(categoryRepository.save(category));
    }

    /** Admin: cập nhật category */
    @Transactional
    public CategoryResponse update(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (request.getCode() != null && !request.getCode().isBlank()) {
            String newCode = request.getCode().toUpperCase();
            if (categoryRepository.existsByCodeAndIdNot(newCode, id)) {
                throw new BadRequestException("Category code already exists: " + newCode);
            }
            category.setCode(newCode);
        }

        if (request.getName() != null) {
            category.setName(request.getName());
        }

        if (Boolean.TRUE.equals(request.getRemoveParent())) {
            category.setParent(null);
        } else if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BadRequestException("Category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
            category.setParent(parent);
        }

        if (request.getDetailType() != null) {
            if (request.getDetailType().isBlank()) {
                category.setDetailType(null);
            } else {
                category.setDetailType(resolveDetailType(request.getDetailType()));
            }
        }

        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }

        return toResponse(categoryRepository.save(category));
    }

    /** Admin: xoá cứng — xoá khỏi DB, báo lỗi nếu còn tin đăng */
    @Transactional
    public void delete(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Kiểm tra danh mục con
        List<Category> children = categoryRepository.findByParentId(id);
        for (Category child : children) {
            if (listingRepository.existsByCategoryId(child.getId())) {
                throw new BadRequestException("Danh mục con \"" + child.getName() + "\" còn tin đăng, không thể xóa");
            }
        }

        // Kiểm tra chính danh mục này
        if (listingRepository.existsByCategoryId(id)) {
            throw new BadRequestException("Danh mục \"" + category.getName() + "\" còn tin đăng, không thể xóa");
        }

        // Xóa con trước, rồi xóa cha
        categoryRepository.deleteAll(children);
        categoryRepository.delete(category);
    }

    /** Lookup detailType by code (case-insensitive). Null/blank → null. */
    private CategoryDetailType resolveDetailType(String code) {
        if (code == null || code.isBlank()) return null;
        return detailTypeRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BadRequestException("Invalid detailType: " + code));
    }

    private CategoryResponse toResponseAll(Category category) {
        List<CategoryResponse> children = categoryRepository
                .findByParentIdOrderBySortOrderAsc(category.getId())
                .stream()
                .map(this::toResponseAll)
                .toList();
        CategoryDetailType dt = category.getDetailType();
        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .detailTypeId(dt != null ? dt.getId() : null)
                .detailType(dt != null ? dt.getCode() : null)
                .detailTypeName(dt != null ? dt.getName() : null)
                .active(category.isActive())
                .sortOrder(category.getSortOrder())
                .children(children)
                .build();
    }

    private CategoryResponse toResponse(Category category) {
        List<CategoryResponse> children = categoryRepository
                .findByParentIdAndActiveTrueOrderBySortOrderAsc(category.getId())
                .stream()
                .map(this::toResponse)
                .toList();

        CategoryDetailType dt = category.getDetailType();
        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .detailTypeId(dt != null ? dt.getId() : null)
                .detailType(dt != null ? dt.getCode() : null)
                .detailTypeName(dt != null ? dt.getName() : null)
                .active(category.isActive())
                .sortOrder(category.getSortOrder())
                .children(children)
                .build();
    }
}
