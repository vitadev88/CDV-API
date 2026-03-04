package com.congdongviet.auth.repository;

import com.congdongviet.auth.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(String phoneNumber);

    /**
     * Count active (non-verified, non-expired) OTP records created within a time window.
     * Used to enforce max-3-resends-per-hour.
     */
    long countByPhoneNumberAndVerifiedFalseAndCreatedAtAfter(String phoneNumber, LocalDateTime since);

    void deleteByPhoneNumberAndVerifiedTrue(String phoneNumber);
}
