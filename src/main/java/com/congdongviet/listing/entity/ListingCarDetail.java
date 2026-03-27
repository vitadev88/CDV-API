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

import java.util.UUID;

@Entity
@Table(name = "listing_car_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingCarDetail {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "manufacture_year", nullable = false)
    private Integer manufactureYear;

    @Column(name = "mileage_km")
    private Integer mileageKm;

    /** GASOLINE | DIESEL | ELECTRIC | HYBRID */
    @Column(name = "fuel_type", length = 20)
    private String fuelType;

    /** MANUAL | AUTOMATIC */
    @Column(length = 20)
    private String transmission;

    @Column(length = 50)
    private String color;
}
