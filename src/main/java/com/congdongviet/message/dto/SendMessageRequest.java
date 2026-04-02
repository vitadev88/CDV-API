package com.congdongviet.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    @NotBlank(message = "Content is required")
    private String content;

    private String imageUrl;
}
