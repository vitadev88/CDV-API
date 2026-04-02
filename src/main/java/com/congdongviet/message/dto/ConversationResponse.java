package com.congdongviet.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ConversationResponse {
    private UUID id;
    private UUID listingId;
    private String listingTitle;
    private String listingCoverImage;
    private List<ParticipantInfo> participants;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private long unreadCount;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ParticipantInfo {
        private UUID id;
        private String fullName;
        private String avatarUrl;
    }
}
