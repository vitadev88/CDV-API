package com.congdongviet.auth.service;

import com.congdongviet.auth.dto.AuthResponse;
import com.congdongviet.auth.dto.LoginRequest;
import com.congdongviet.auth.dto.RegisterRequest;
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

    /**
     * Registers a new user with either email or phone (at least one required).
     * Account is immediately active — no OTP verification needed.
     */

    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    public boolean isPhoneAvailable(String phone) {
        return !userRepository.existsByPhone(phone);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already in use");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getUsername())
                .role(Role.USER)
                .verified(true)
                .build();

        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(
                user.getId(),
                user.getPhone(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                true
        );
        String token = jwtTokenProvider.generateToken(principal);

        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .username(user.getUsername())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
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
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .build();
    }
}
