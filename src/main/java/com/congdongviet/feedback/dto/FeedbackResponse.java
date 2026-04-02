package com.congdongviet.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class FeedbackResponse {

    private UUID id;
    private String content;
    private String category;
    private String senderName;
    private String senderEmail;
    private UUID userId;
    private String userFullName;
    private String status;
    private LocalDateTime createdAt;
}
