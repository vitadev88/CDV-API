package com.congdongviet.category.service;

import com.congdongviet.category.dto.CategoryDetailTypeResponse;
import com.congdongviet.category.dto.CreateCategoryDetailTypeRequest;
import com.congdongviet.category.dto.UpdateCategoryDetailTypeRequest;
import com.congdongviet.category.entity.CategoryDetailType;
import com.congdongviet.category.repository.CategoryDetailTypeRepository;
import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryDetailTypeService {

    private final CategoryDetailTypeRepository repository;

    public List<CategoryDetailTypeResponse> getAll() {
        return repository.findAll(Sort.by("sortOrder", "name"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDetailTypeResponse getById(UUID id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CategoryDetailType", "id", id)));
    }

    @Transactional
    public CategoryDetailTypeResponse create(CreateCategoryDetailTypeRequest request) {
        String code = request.getCode().toUpperCase();
        if (repository.existsByCode(code)) {
            throw new BadRequestException("Detail type code already exists: " + code);
        }
        CategoryDetailType entity = CategoryDetailType.builder()
                .code(code)
                .name(request.getName())
                .sortOrder(request.getSortOrder())
                .build();
        return toResponse(repository.save(entity));
    }

    @Transactional
    public CategoryDetailTypeResponse update(UUID id, UpdateCategoryDetailTypeRequest request) {
        CategoryDetailType entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CategoryDetailType", "id", id));

        if (request.getCode() != null && !request.getCode().isBlank()) {
            String code = request.getCode().toUpperCase();
            if (repository.existsByCodeAndIdNot(code, id)) {
                throw new BadRequestException("Detail type code already exists: " + code);
            }
            entity.setCode(code);
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            entity.setName(request.getName());
        }
        if (request.getSortOrder() != null) {
            entity.setSortOrder(request.getSortOrder());
        }
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("CategoryDetailType", "id", id);
        }
        repository.deleteById(id);
    }

    private CategoryDetailTypeResponse toResponse(CategoryDetailType e) {
        return CategoryDetailTypeResponse.builder()
                .id(e.getId())
                .code(e.getCode())
                .name(e.getName())
                .sortOrder(e.getSortOrder())
                .build();
    }
}
