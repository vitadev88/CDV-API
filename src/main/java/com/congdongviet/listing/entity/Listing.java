package com.congdongviet.listing.entity;

import com.congdongviet.auth.entity.User;
import com.congdongviet.category.entity.Category;
import com.congdongviet.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Listing extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_unit", nullable = false, length = 20)
    @Builder.Default
    private PriceUnit priceUnit = PriceUnit.TOTAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ListingStatus status = ListingStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToOne(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ListingRealEstateDetail realEstateDetail;

    @OneToOne(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ListingCarDetail carDetail;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ListingImage> images = new ArrayList<>();
}
