package com.congdongviet.feedback.controller;

import com.congdongviet.common.dto.ApiResponse;
import com.congdongviet.feedback.dto.CreateFeedbackRequest;
import com.congdongviet.feedback.dto.FeedbackResponse;
import com.congdongviet.feedback.service.FeedbackService;
import com.congdongviet.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /** Gửi góp ý — public, không cần đăng nhập */
    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackResponse>> create(
            @Valid @RequestBody CreateFeedbackRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        UUID userId = currentUser != null ? currentUser.getId() : null;
        FeedbackResponse data = feedbackService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "Cảm ơn bạn đã góp ý!"));
    }

    /** Danh sách góp ý — chỉ ADMIN */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<FeedbackResponse>>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getAll(pageable), "OK"));
    }

    /** Số góp ý chưa đọc */
    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countUnread() {
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("count", feedbackService.countUnread()), "OK"));
    }

    /** Đánh dấu đã đọc */
    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.markAsRead(id), "Đã đánh dấu đọc"));
    }
}
