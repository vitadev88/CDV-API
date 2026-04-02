package com.congdongviet.feedback.service;

import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.common.exception.ResourceNotFoundException;
import com.congdongviet.feedback.dto.CreateFeedbackRequest;
import com.congdongviet.feedback.dto.FeedbackResponse;
import com.congdongviet.feedback.entity.Feedback;
import com.congdongviet.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Transactional
    public FeedbackResponse create(CreateFeedbackRequest request, UUID currentUserId) {
        Feedback.FeedbackCategory category;
        try {
            category = Feedback.FeedbackCategory.valueOf(
                    request.getCategory() != null ? request.getCategory().toUpperCase() : "OTHER");
        } catch (IllegalArgumentException e) {
            category = Feedback.FeedbackCategory.OTHER;
        }

        User user = null;
        if (currentUserId != null) {
            user = userRepository.findById(currentUserId).orElse(null);
        }

        Feedback feedback = Feedback.builder()
                .content(request.getContent())
                .category(category)
                .senderName(request.getSenderName())
                .senderEmail(request.getSenderEmail())
                .user(user)
                .status(Feedback.FeedbackStatus.NEW)
                .build();

        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional(readOnly = true)
    public Page<FeedbackResponse> getAll(Pageable pageable) {
        return feedbackRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    @Transactional
    public FeedbackResponse markAsRead(UUID id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));
        feedback.setStatus(Feedback.FeedbackStatus.READ);
        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional(readOnly = true)
    public long countUnread() {
        return feedbackRepository.countByStatus(Feedback.FeedbackStatus.NEW);
    }

    private FeedbackResponse toResponse(Feedback f) {
        return FeedbackResponse.builder()
                .id(f.getId())
                .content(f.getContent())
                .category(f.getCategory().name())
                .senderName(f.getSenderName())
                .senderEmail(f.getSenderEmail())
                .userId(f.getUser() != null ? f.getUser().getId() : null)
                .userFullName(f.getUser() != null ? f.getUser().getFullName() : null)
                .status(f.getStatus().name())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
