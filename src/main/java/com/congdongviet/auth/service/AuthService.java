package com.congdongviet.auth.service;

import com.congdongviet.auth.dto.AuthResponse;
import com.congdongviet.auth.dto.LoginRequest;
import com.congdongviet.auth.dto.RegisterRequest;
import com.congdongviet.auth.dto.VerifyOtpRequest;
import com.congdongviet.auth.entity.Role;
import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.security.JwtTokenProvider;
import com.congdongviet.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;

    /**
     * Registers a new user and sends an OTP to their phone number.
     * The account is inactive (verified=false) until OTP is confirmed.
     */
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(Role.USER)
                .verified(false)
                .build();

        userRepository.save(user);
        otpService.generateAndSend(request.getPhone());
    }

    /**
     * Verifies the OTP, activates the account, and returns a JWT token.
     */
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        otpService.validateOtp(request.getPhone(), request.getOtpCode());

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));

        user.setVerified(true);
        userRepository.save(user);

        otpService.clearOtp(request.getPhone());

        // Build principal and generate JWT without re-authenticating (no password needed)
        UserPrincipal principal = new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                true
        );
        String token = jwtTokenProvider.generateToken(principal);

        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BadRequestException("User not found"));

        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}
