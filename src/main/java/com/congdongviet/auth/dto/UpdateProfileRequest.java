package com.congdongviet.auth.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;

    @Size(max = 500, message = "Bio must be at most 500 characters")
    private String bio;

    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;

    private LocalDate dateOfBirth;

    @Size(max = 100, message = "Zalo must be at most 100 characters")
    private String zalo;

    @Size(max = 100, message = "Line ID must be at most 100 characters")
    private String lineId;

    @Size(max = 255, message = "Facebook must be at most 255 characters")
    private String facebook;

    @Size(max = 255, message = "TikTok must be at most 255 characters")
    private String tiktok;
}
