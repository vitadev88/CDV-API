package com.congdongviet.donation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DonationAccountRequest {

    @NotBlank(message = "Bank name is required")
    @Size(max = 100)
    private String bankName;

    @NotBlank(message = "Account number is required")
    @Size(max = 50)
    private String accountNumber;

    @NotBlank(message = "Account holder is required")
    @Size(max = 100)
    private String accountHolder;

    @Size(max = 500)
    private String qrImageUrl;

    private boolean active = true;

    private int sortOrder = 0;
}
