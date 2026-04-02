package com.congdongviet.auth.entity;

import com.congdongviet.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    @Column(length = 255)
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String zalo;

    @Column(name = "line_id", length = 100)
    private String lineId;

    @Column(length = 255)
    private String facebook;

    @Column(length = 255)
    private String tiktok;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false;

    /** Login identifier for admin/system accounts (not used by regular users). */
    @Column(unique = true, length = 100)
    private String username;

    /** Records who created this account (e.g. "SYSTEM_BOOTSTRAP", user ID). */
    @Column(name = "created_by", length = 100)
    private String createdBy;
}
