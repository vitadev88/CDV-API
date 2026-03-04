package com.congdongviet.auth.service;

import com.congdongviet.auth.twilio.TwilioSmsService;
import com.congdongviet.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    // Redis key prefixes
    private static final String KEY_OTP_CODE     = "otp:code:";
    private static final String KEY_OTP_ATTEMPTS = "otp:attempts:";
    private static final String KEY_OTP_RESEND   = "otp:resend:";

    private static final Duration OTP_TTL        = Duration.ofMinutes(5);
    private static final Duration RESEND_TTL     = Duration.ofHours(1);
    private static final int MAX_RESEND_PER_HOUR = 3;
    private static final int MAX_VERIFY_ATTEMPTS = 5;

    private final RedisTemplate<String, String> redisTemplate;
    private final TwilioSmsService twilioSmsService;

    /**
     * Generates an OTP, stores it in Redis with TTL, and sends SMS.
     * Enforces max-3-resends-per-hour using a Redis counter with 1-hour TTL.
     */
    public void generateAndSend(String phoneNumber) {
        enforceResendLimit(phoneNumber);

        String otpCode = generateOtpCode();
        String codeKey     = KEY_OTP_CODE + phoneNumber;
        String attemptsKey = KEY_OTP_ATTEMPTS + phoneNumber;

        // Store OTP and reset attempt counter — must happen before async SMS dispatch
        redisTemplate.opsForValue().set(codeKey, otpCode, OTP_TTL);
        redisTemplate.delete(attemptsKey);

        // Fire-and-forget: HTTP response returns immediately, SMS sent in background.
        // If Twilio fails, OTP stays in Redis and user can request a resend.
        twilioSmsService.sendOtp(phoneNumber, otpCode)
                .exceptionally(ex -> {
                    log.error("SMS delivery failed for phone: {} — {}", phoneNumber, ex.getMessage());
                    return null;
                });

        log.info("OTP stored in Redis and SMS dispatched for phone: {}", phoneNumber);
    }

    /**
     * Validates OTP from Redis.
     * Increments attempt counter on failure.
     * Throws BadRequestException with generic messages to prevent enumeration.
     */
    public void validateOtp(String phoneNumber, String otpCode) {
        String codeKey     = KEY_OTP_CODE + phoneNumber;
        String attemptsKey = KEY_OTP_ATTEMPTS + phoneNumber;

        // Check attempt limit first (brute-force guard)
        String attemptsVal = redisTemplate.opsForValue().get(attemptsKey);
        int attempts = attemptsVal == null ? 0 : Integer.parseInt(attemptsVal);
        if (attempts >= MAX_VERIFY_ATTEMPTS) {
            log.warn("Max verify attempts reached for phone: {}", phoneNumber);
            throw new BadRequestException("Invalid or expired OTP");
        }

        String storedCode = redisTemplate.opsForValue().get(codeKey);

        // Null means OTP expired or was never issued
        if (storedCode == null) {
            log.warn("OTP not found in Redis for phone: {}", phoneNumber);
            throw new BadRequestException("Invalid or expired OTP");
        }

        if (!storedCode.equals(otpCode)) {
            Long newAttempts = redisTemplate.opsForValue().increment(attemptsKey);
            // Sync attempts TTL with OTP TTL on first failure
            if (newAttempts != null && newAttempts == 1) {
                redisTemplate.expire(attemptsKey, OTP_TTL);
            }
            log.warn("Invalid OTP attempt #{} for phone: {}", newAttempts, phoneNumber);
            throw new BadRequestException("Invalid or expired OTP");
        }
    }

    /**
     * Deletes all OTP-related Redis keys after successful verification.
     */
    public void clearOtp(String phoneNumber) {
        redisTemplate.delete(KEY_OTP_CODE + phoneNumber);
        redisTemplate.delete(KEY_OTP_ATTEMPTS + phoneNumber);
        log.info("OTP keys cleared for phone: {}", phoneNumber);
    }

    // -------------------------------------------------------------------------

    private void enforceResendLimit(String phoneNumber) {
        String resendKey = KEY_OTP_RESEND + phoneNumber;

        Long count = redisTemplate.opsForValue().increment(resendKey);

        // Set expiry only on the first increment (fixed 1-hour window)
        if (count != null && count == 1) {
            redisTemplate.expire(resendKey, RESEND_TTL);
        }

        if (count != null && count > MAX_RESEND_PER_HOUR) {
            log.warn("OTP resend limit exceeded for phone: {}", phoneNumber);
            throw new BadRequestException("Invalid or expired OTP");
        }
    }

    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        int code = 100_000 + random.nextInt(900_000);
        return String.valueOf(code);
    }
}
