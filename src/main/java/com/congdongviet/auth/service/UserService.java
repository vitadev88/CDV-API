package com.congdongviet.auth.service;

import com.congdongviet.auth.dto.ChangeRoleRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
                .username(user.getUsername())
                .role(user.getRole().name())
                .verified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
