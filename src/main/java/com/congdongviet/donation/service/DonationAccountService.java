package com.congdongviet.donation.service;

import com.congdongviet.common.exception.ResourceNotFoundException;
import com.congdongviet.donation.dto.DonationAccountRequest;
import com.congdongviet.donation.dto.DonationAccountResponse;
import com.congdongviet.donation.entity.DonationAccount;
import com.congdongviet.donation.repository.DonationAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonationAccountService {

    private final DonationAccountRepository repository;

    @Transactional(readOnly = true)
    public List<DonationAccountResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DonationAccountResponse> getActive() {
        return repository.findByActiveTrueOrderBySortOrderAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public DonationAccountResponse create(DonationAccountRequest request) {
        DonationAccount account = DonationAccount.builder()
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountHolder(request.getAccountHolder())
                .qrImageUrl(request.getQrImageUrl())
                .active(request.isActive())
                .sortOrder(request.getSortOrder())
                .build();
        return toResponse(repository.save(account));
    }

    @Transactional
    public DonationAccountResponse update(UUID id, DonationAccountRequest request) {
        DonationAccount account = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DonationAccount", "id", id));
        account.setBankName(request.getBankName());
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountHolder(request.getAccountHolder());
        account.setQrImageUrl(request.getQrImageUrl());
        account.setActive(request.isActive());
        account.setSortOrder(request.getSortOrder());
        return toResponse(repository.save(account));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("DonationAccount", "id", id);
        }
        repository.deleteById(id);
    }

    private DonationAccountResponse toResponse(DonationAccount a) {
        return DonationAccountResponse.builder()
                .id(a.getId())
                .bankName(a.getBankName())
                .accountNumber(a.getAccountNumber())
                .accountHolder(a.getAccountHolder())
                .qrImageUrl(a.getQrImageUrl())
                .active(a.isActive())
                .sortOrder(a.getSortOrder())
                .build();
    }
}
