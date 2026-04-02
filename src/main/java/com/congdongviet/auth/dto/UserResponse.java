package com.congdongviet.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String bio;
    private String address;
    private LocalDate dateOfBirth;
    private String zalo;
    private String lineId;
    private String facebook;
    private String tiktok;
    private String username;
    private String role;
    private boolean verified;
    private LocalDateTime createdAt;
}
