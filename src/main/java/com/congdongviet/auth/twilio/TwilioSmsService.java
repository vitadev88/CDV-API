package com.congdongviet.auth.twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public void sendOtp(String toPhoneNumber, String otpCode) {
        String messageBody = "Your CongDongViet verification code is: " + otpCode
                + ". It expires in 5 minutes. Do not share this code.";

        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(twilioProperties.getPhoneNumber()),
                messageBody
        ).create();

        log.info("OTP SMS sent to {} | SID: {}", toPhoneNumber, message.getSid());
    }
}
