package com.congdongviet.auth.service;

import com.congdongviet.auth.entity.OtpVerification;
import com.congdongviet.auth.repository.OtpRepository;
import com.congdongviet.auth.twilio.TwilioSmsService;
import com.congdongviet.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_RESEND_PER_HOUR = 3;
    private static final int MAX_VERIFY_ATTEMPTS = 5;

    private final OtpRepository otpRepository;
    private final TwilioSmsService twilioSmsService;

    /**
     * Generates a new OTP, persists it, and sends it via SMS.
     * Enforces max-3-resends-per-hour limit.
     */
    @Transactional
    public void generateAndSend(String phoneNumber) {
        long recentCount = otpRepository.countByPhoneNumberAndVerifiedFalseAndCreatedAtAfter(
                phoneNumber, LocalDateTime.now().minusHours(1));

        if (recentCount >= MAX_RESEND_PER_HOUR) {
            log.warn("OTP resend limit exceeded for phone: {}", phoneNumber);
            throw new BadRequestException("Too many OTP requests. Please wait before requesting again.");
        }

        String otpCode = generateOtpCode();

        OtpVerification otp = OtpVerification.builder()
                .phoneNumber(phoneNumber)
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();

        otpRepository.save(otp);
        twilioSmsService.sendOtp(phoneNumber, otpCode);

        log.info("OTP generated and sent for phone: {}", phoneNumber);
    }

    /**
     * Validates the OTP for a given phone number.
     * Enforces max-5-verify-attempts brute-force protection.
     *
     * @return the matched OtpVerification record (not yet marked verified — caller must do that)
     */
    @Transactional
    public OtpVerification validateOtp(String phoneNumber, String otpCode) {
        OtpVerification otp = otpRepository
                .findTopByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(phoneNumber)
                .orElseThrow(() -> {
                    log.warn("No pending OTP found for phone: {}", phoneNumber);
                    return new BadRequestException("Invalid or expired OTP");
                });

        if (otp.getVerifyAttempts() >= MAX_VERIFY_ATTEMPTS) {
            log.warn("Max verify attempts reached for phone: {}", phoneNumber);
            throw new BadRequestException("Too many failed attempts. Please request a new OTP.");
        }

        if (otp.isExpired()) {
            log.warn("OTP expired for phone: {}", phoneNumber);
            throw new BadRequestException("Invalid or expired OTP");
        }

        if (!otp.getOtpCode().equals(otpCode)) {
            otp.setVerifyAttempts(otp.getVerifyAttempts() + 1);
            otpRepository.save(otp);
            log.warn("Invalid OTP attempt #{} for phone: {}", otp.getVerifyAttempts(), phoneNumber);
            throw new BadRequestException("Invalid or expired OTP");
        }

        return otp;
    }

    @Transactional
    public void markVerified(OtpVerification otp) {
        otp.setVerified(true);
        otpRepository.save(otp);
        log.info("OTP verified and deleted for phone: {}", otp.getPhoneNumber());
        otpRepository.deleteByPhoneNumberAndVerifiedTrue(otp.getPhoneNumber());
    }

    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        int code = 100_000 + random.nextInt(900_000);
        return String.valueOf(code);
    }
}
