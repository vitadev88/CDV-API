package com.congdongviet.auth.service;

import com.congdongviet.auth.dto.ChangeRoleRequest;
import com.congdongviet.auth.dto.CreateUserRequest;
import com.congdongviet.auth.dto.PublicUserResponse;
import com.congdongviet.auth.dto.UpdateProfileRequest;
import com.congdongviet.auth.dto.UpdateUserRequest;
import com.congdongviet.auth.dto.UserResponse;
import com.congdongviet.auth.entity.Role;
import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(CreateUserRequest request, String createdBy) {
        if (request.getEmail() == null && request.getPhone() == null) {
            throw new BadRequestException("Email or phone is required");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank() && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (request.getPhone() != null && !request.getPhone().isBlank() && userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already in use");
        }

        Role role = Role.USER;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid role: " + request.getRole());
            }
            if (role == Role.SUPER_ADMIN) {
                throw new BadRequestException("Cannot create SUPER_ADMIN account");
            }
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail() != null && !request.getEmail().isBlank() ? request.getEmail() : null)
                .phone(request.getPhone() != null && !request.getPhone().isBlank() ? request.getPhone() : null)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .verified(true)
                .createdBy(createdBy)
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(String keyword, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return userRepository.search(kw, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = findOrThrow(id);

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            if (!request.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
                throw new BadRequestException("Phone number already in use");
            }
            user.setPhone(request.getPhone());
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse changeRole(UUID id, ChangeRoleRequest request) {
        User user = findOrThrow(id);

        Role newRole;
        try {
            newRole = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + request.getRole());
        }

        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new BadRequestException("Cannot change role of SUPER_ADMIN");
        }

        user.setRole(newRole);
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        User user = findOrThrow(id);
        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new BadRequestException("Cannot delete SUPER_ADMIN account");
        }
        userRepository.delete(user);
    }

    // ── Public profile ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PublicUserResponse getPublicProfile(UUID id) {
        User user = findOrThrow(id);
        return PublicUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .phone(user.getPhone())
                .address(user.getAddress())
                .zalo(user.getZalo())
                .lineId(user.getLineId())
                .facebook(user.getFacebook())
                .tiktok(user.getTiktok())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // ── Profile endpoints (self) ────────────────────────────────────────

    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID userId) {
        return toResponse(findOrThrow(userId));
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = findOrThrow(userId);

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) {
            if (!request.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
                throw new BadRequestException("Phone number already in use");
            }
            user.setPhone(request.getPhone());
        }
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getZalo() != null) user.setZalo(request.getZalo());
        if (request.getLineId() != null) user.setLineId(request.getLineId());
        if (request.getFacebook() != null) user.setFacebook(request.getFacebook());
        if (request.getTiktok() != null) user.setTiktok(request.getTiktok());

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateAvatar(UUID userId, String avatarUrl) {
        User user = findOrThrow(userId);
        user.setAvatarUrl(avatarUrl);
        return toResponse(userRepository.save(user));
    }

    private User findOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .zalo(user.getZalo())
                .lineId(user.getLineId())
                .facebook(user.getFacebook())
                .tiktok(user.getTiktok())
                .username(user.getUsername())
                .role(user.getRole().name())
                .verified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
