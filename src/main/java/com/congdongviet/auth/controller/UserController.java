package com.congdongviet.auth.controller;

import com.congdongviet.auth.dto.ChangeRoleRequest;
import com.congdongviet.auth.dto.UpdateUserRequest;
import com.congdongviet.auth.dto.UserResponse;
import com.congdongviet.auth.service.UserService;
import com.congdongviet.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** Danh sách user, hỗ trợ tìm kiếm theo tên/email/sđt */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(ApiResponse.success(userService.getAll(keyword, pageable), "OK"));
    }

    /** Chi tiết 1 user */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id), "OK"));
    }

    /** Cập nhật thông tin user (tên, email, sđt) */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(ApiResponse.success(userService.update(id, request), "User updated successfully"));
    }

    /** Đổi role user — chỉ SUPER_ADMIN */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> changeRole(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeRoleRequest request) {

        return ResponseEntity.ok(ApiResponse.success(userService.changeRole(id, request), "Role updated successfully"));
    }

    /** Xoá user — chỉ SUPER_ADMIN */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }
}
