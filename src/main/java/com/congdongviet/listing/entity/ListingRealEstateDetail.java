package com.congdongviet.listing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "listing_real_estate_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingRealEstateDetail {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String district;

    @Column(length = 255)
    private String address;

    @Column(name = "area_sqm", precision = 8, scale = 2)
    private BigDecimal areaSqm;

    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floors;

    @Column(name = "is_furnished", nullable = false)
    @Builder.Default
    private boolean furnished = false;

    /** Chỉ dùng cho cho thuê */
    @Column(name = "available_from")
    private LocalDate availableFrom;

    /** Tiền cọc — chỉ dùng cho cho thuê */
    @Column(precision = 15, scale = 2)
    private BigDecimal deposit;
}
