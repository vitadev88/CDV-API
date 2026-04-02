package com.congdongviet.admin.service;

import com.congdongviet.admin.entity.AuditLog;
import com.congdongviet.admin.repository.AuditLogRepository;
import com.congdongviet.auth.entity.Role;
import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.config.SuperAdminProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private static final String BOOTSTRAP_ACTOR  = "SYSTEM_BOOTSTRAP";
    private static final String BOOTSTRAP_ACTION = "SYSTEM_BOOTSTRAP_CREATED_SUPER_ADMIN";

    private final UserRepository        userRepository;
    private final AuditLogRepository    auditLogRepository;
    private final PasswordEncoder       passwordEncoder;
    private final SuperAdminProperties  properties;

    /**
     * Idempotent bootstrap: creates the SUPER_ADMIN account if it does not exist.
     *
     * Concurrency safety:
     *  - Existence check with existsByUsername reduces unnecessary write attempts.
     *  - Unique constraint on `username` column is the true guard against race conditions
     *    when multiple application instances start simultaneously.
     *  - DataIntegrityViolationException is caught and treated as "already exists".
     */
    @Transactional
    public void bootstrapSuperAdmin() {
        String username = properties.getUsername();

        if (userRepository.existsByUsername(username)) {
            log.warn("[BOOTSTRAP] SUPER_ADMIN '{}' already exists — skipping", username);
            return;
        }

        try {
            User superAdmin = User.builder()
                    .username(username)
                    .fullName(properties.getFullName())
                    .email(properties.getEmail())
                    .password(passwordEncoder.encode(properties.getPassword()))
                    .phone(properties.getPhone())
                    .role(Role.SUPER_ADMIN)
                    .verified(true)
                    .createdBy(BOOTSTRAP_ACTOR)
                    .build();

            userRepository.save(superAdmin);

            auditLogRepository.save(AuditLog.builder()
                    .action(BOOTSTRAP_ACTION)
                    .actor(BOOTSTRAP_ACTOR)
                    .build());

            log.info("[BOOTSTRAP] SUPER_ADMIN '{}' created successfully", username);

        } catch (DataIntegrityViolationException e) {
            // Another instance won the race — this is expected in multi-instance deployments
            log.warn("[BOOTSTRAP] SUPER_ADMIN '{}' was created concurrently by another instance", username);

        } catch (Exception e) {
            log.error("[BOOTSTRAP] Failed to create SUPER_ADMIN '{}': {}", username, e.getMessage(), e);
            throw e;
        }
    }
}
