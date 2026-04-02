package com.congdongviet.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFeedbackRequest {

    @NotBlank(message = "Nội dung góp ý không được để trống")
    @Size(max = 2000, message = "Nội dung tối đa 2000 ký tự")
    private String content;

    /** BUG | SUGGESTION | OTHER */
    private String category = "OTHER";

    @Size(max = 100)
    private String senderName;

    @Size(max = 150)
    private String senderEmail;
}
