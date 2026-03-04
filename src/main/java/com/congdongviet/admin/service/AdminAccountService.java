package com.congdongviet.admin.service;

import com.congdongviet.admin.dto.AdminAccountResponse;
import com.congdongviet.admin.dto.CreateAdminRequest;
import com.congdongviet.admin.entity.AuditLog;
import com.congdongviet.admin.repository.AuditLogRepository;
import com.congdongviet.auth.entity.Role;
import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAccountService {

    private final UserRepository     userRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder    passwordEncoder;

    @Transactional
    public AdminAccountResponse createAdmin(CreateAdminRequest request, String createdByUsername) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (request.getUsername() != null && userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already in use");
        }
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already in use");
        }

        User admin = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(Role.ADMIN)
                .verified(true)
                .createdBy(createdByUsername)
                .build();

        userRepository.save(admin);

        auditLogRepository.save(AuditLog.builder()
                .action("SUPER_ADMIN_CREATED_ADMIN_ACCOUNT")
                .actor(createdByUsername)
                .build());

        log.info("[ADMIN_MGMT] ADMIN account '{}' created by '{}'", request.getEmail(), createdByUsername);

        return toResponse(admin);
    }

    private AdminAccountResponse toResponse(User user) {
        return AdminAccountResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .createdBy(user.getCreatedBy())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
