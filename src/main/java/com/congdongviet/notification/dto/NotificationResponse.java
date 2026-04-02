package com.congdongviet.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String type;
    private String title;
    private String content;
    private UUID referenceId;
    private String referenceType;
    private boolean read;
    private LocalDateTime createdAt;
}
