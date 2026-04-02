package com.congdongviet.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {

    private UUID id;
    private String token;
    private String username;
    private String phone;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String role;
}
