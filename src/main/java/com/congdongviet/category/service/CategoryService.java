package com.congdongviet.category.service;

import com.congdongviet.category.dto.CategoryResponse;
import com.congdongviet.category.entity.Category;
import com.congdongviet.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /** Trả về toàn bộ cây danh mục (root + children) */
    public List<CategoryResponse> getTree() {
        return categoryRepository.findByParentIsNullAndActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
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
                .sortOrder(category.getSortOrder())
                .children(children)
                .build();
    }
}
