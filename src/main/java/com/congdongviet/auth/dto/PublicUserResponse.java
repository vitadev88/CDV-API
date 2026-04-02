package com.congdongviet.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PublicUserResponse {

    private UUID id;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String phone;
    private String address;
    private String zalo;
    private String lineId;
    private String facebook;
    private String tiktok;
    private LocalDateTime createdAt;
}
