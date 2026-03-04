package com.congdongviet.auth.twilio;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwilioSmsService {

    private final TwilioProperties twilioProperties;

    @PostConstruct
    private void init() {
        Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
        log.info("Twilio client initialized");
    }

    /**
     * Sends OTP SMS asynchronously using the dedicated smsExecutor thread pool.
     * Returns CompletableFuture so callers can attach error handlers without blocking.
     *
     * Failure does NOT affect the HTTP response — OTP is already stored in Redis
     * and the user can request a resend.
     */
    @Async("smsExecutor")
    public CompletableFuture<Void> sendOtp(String toPhoneNumber, String otpCode) {
        try {
            String body = "Your CongDongViet verification code is: " + otpCode
                    + ". It expires in 5 minutes. Do not share this code.";

            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(twilioProperties.getPhoneNumber()),
                    body
            ).create();

            log.info("OTP SMS sent to {} | SID: {}", toPhoneNumber, message.getSid());
            return CompletableFuture.completedFuture(null);

        } catch (ApiException e) {
            log.error("Twilio API error sending OTP to {} | code={} message={}",
                    toPhoneNumber, e.getCode(), e.getMessage());
            return CompletableFuture.failedFuture(e);

        } catch (Exception e) {
            log.error("Unexpected error sending OTP to {}: {}", toPhoneNumber, e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
