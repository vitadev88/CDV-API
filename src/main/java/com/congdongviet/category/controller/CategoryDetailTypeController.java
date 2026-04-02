package com.congdongviet.category.controller;

import com.congdongviet.category.dto.CategoryDetailTypeResponse;
import com.congdongviet.category.dto.CreateCategoryDetailTypeRequest;
import com.congdongviet.category.dto.UpdateCategoryDetailTypeRequest;
import com.congdongviet.category.service.CategoryDetailTypeService;
import com.congdongviet.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/category-detail-types")
@RequiredArgsConstructor
public class CategoryDetailTypeController {

    private final CategoryDetailTypeService service;

    /** Public — frontend dùng để populate dropdown */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDetailTypeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(), "OK"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDetailTypeResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id), "OK"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDetailTypeResponse>> create(
            @Valid @RequestBody CreateCategoryDetailTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request), "Created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDetailTypeResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryDetailTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted successfully"));
    }
}
