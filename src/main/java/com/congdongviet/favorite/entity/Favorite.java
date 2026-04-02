package com.congdongviet.favorite.entity;

import com.congdongviet.auth.entity.User;
import com.congdongviet.common.entity.BaseEntity;
import com.congdongviet.listing.entity.Listing;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "favorites")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;
}
