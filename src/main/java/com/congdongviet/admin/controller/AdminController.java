package com.congdongviet.admin.controller;

import com.congdongviet.admin.dto.AdminAccountResponse;
import com.congdongviet.admin.dto.CreateAdminRequest;
import com.congdongviet.admin.dto.DashboardStatsResponse;
import com.congdongviet.admin.service.AdminAccountService;
import com.congdongviet.admin.service.DashboardService;
import com.congdongviet.common.dto.ApiResponse;
import com.congdongviet.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminAccountService adminAccountService;
    private final DashboardService dashboardService;

    @PostMapping("/accounts")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> createAdmin(
            @Valid @RequestBody CreateAdminRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        AdminAccountResponse data = adminAccountService.createAdmin(request, currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "Admin account created successfully"));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getStats(), "OK"));
    }
}
