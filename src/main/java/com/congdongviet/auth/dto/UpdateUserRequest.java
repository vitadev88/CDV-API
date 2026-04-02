package com.congdongviet.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateUserRequest {

    @Size(max = 100)
    private String fullName;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String phone;
}
