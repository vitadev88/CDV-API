package com.congdongviet.donation.repository;

import com.congdongviet.donation.entity.DonationAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DonationAccountRepository extends JpaRepository<DonationAccount, UUID> {

    List<DonationAccount> findByActiveTrueOrderBySortOrderAsc();
}
