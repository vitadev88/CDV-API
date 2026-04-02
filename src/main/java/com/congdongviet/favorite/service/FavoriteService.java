package com.congdongviet.favorite.service;

import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.common.exception.ResourceNotFoundException;
import com.congdongviet.favorite.entity.Favorite;
import com.congdongviet.favorite.repository.FavoriteRepository;
import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    /**
     * Toggle favorite: if already favorited → remove, else → add.
     * @return true if favorited after toggle, false if unfavorited
     */
    @Transactional
    public boolean toggle(UUID userId, UUID listingId) {
        if (favoriteRepository.existsByUserIdAndListingId(userId, listingId)) {
            favoriteRepository.deleteByUserIdAndListingId(userId, listingId);
            return false;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", listingId));

        Favorite favorite = Favorite.builder()
                .user(user)
                .listing(listing)
                .build();
        favoriteRepository.save(favorite);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isFavorited(UUID userId, UUID listingId) {
        return favoriteRepository.existsByUserIdAndListingId(userId, listingId);
    }

    @Transactional(readOnly = true)
    public Page<Favorite> getMyFavorites(UUID userId, Pageable pageable) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
