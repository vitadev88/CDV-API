package com.congdongviet.donation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DonationAccountResponse {

    private UUID id;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String qrImageUrl;
    private boolean active;
    private int sortOrder;
}
