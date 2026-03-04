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
