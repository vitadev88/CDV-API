package com.congdongviet.featured.entity;

import com.congdongviet.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "featured_packages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeaturedPackage extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private long price = 0;

    @Column(name = "duration_days", nullable = false)
    @Builder.Default
    private int durationDays = 7;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
