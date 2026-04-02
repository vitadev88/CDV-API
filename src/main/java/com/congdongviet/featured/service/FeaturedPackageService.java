package com.congdongviet.featured.service;

import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.common.exception.ResourceNotFoundException;
import com.congdongviet.featured.dto.ApplyPromotionRequest;
import com.congdongviet.featured.dto.FeaturedPackageRequest;
import com.congdongviet.featured.dto.FeaturedPackageResponse;
import com.congdongviet.featured.dto.FeaturedPromotionResponse;
import com.congdongviet.featured.entity.FeaturedPackage;
import com.congdongviet.featured.entity.FeaturedPromotion;
import com.congdongviet.featured.repository.FeaturedPackageRepository;
import com.congdongviet.featured.repository.FeaturedPromotionRepository;
import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.repository.ListingRepository;
import com.congdongviet.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeaturedPackageService {

    private final FeaturedPackageRepository packageRepository;
    private final FeaturedPromotionRepository promotionRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    // ── Packages ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FeaturedPackageResponse> getActivePackages() {
        return packageRepository.findByActiveTrueOrderBySortOrderAsc()
                .stream().map(this::toPackageResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FeaturedPackageResponse> getAllPackages() {
        return packageRepository.findAll().stream().map(this::toPackageResponse).toList();
    }

    @Transactional
    public FeaturedPackageResponse createPackage(FeaturedPackageRequest request) {
        FeaturedPackage pkg = FeaturedPackage.builder()
                .name(request.getName())
                .price(request.getPrice())
                .durationDays(request.getDurationDays())
                .active(request.isActive())
                .sortOrder(request.getSortOrder())
                .build();
        return toPackageResponse(packageRepository.save(pkg));
    }

    @Transactional
    public FeaturedPackageResponse updatePackage(UUID id, FeaturedPackageRequest request) {
        FeaturedPackage pkg = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeaturedPackage", "id", id));
        pkg.setName(request.getName());
        pkg.setPrice(request.getPrice());
        pkg.setDurationDays(request.getDurationDays());
        pkg.setActive(request.isActive());
        pkg.setSortOrder(request.getSortOrder());
        return toPackageResponse(packageRepository.save(pkg));
    }

    @Transactional
    public void deletePackage(UUID id) {
        if (!packageRepository.existsById(id)) {
            throw new ResourceNotFoundException("FeaturedPackage", "id", id);
        }
        packageRepository.deleteById(id);
    }

    // ── Promotions ────────────────────────────────────────────────────────────

    @Transactional
    public FeaturedPromotionResponse applyPromotion(ApplyPromotionRequest request, UserPrincipal currentUser) {
        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", request.getListingId()));

        if (!listing.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Bạn không phải chủ bài đăng này");
        }

        FeaturedPackage pkg = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("FeaturedPackage", "id", request.getPackageId()));

        if (!pkg.isActive()) {
            throw new BadRequestException("Gói này không còn hoạt động");
        }

        if (promotionRepository.existsByListingIdAndEndAtAfter(listing.getId(), LocalDateTime.now())) {
            throw new BadRequestException("Bài đăng này đang được đẩy nổi bật, vui lòng chờ hết hạn");
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        LocalDateTime now = LocalDateTime.now();
        FeaturedPromotion promotion = FeaturedPromotion.builder()
                .listing(listing)
                .featuredPackage(pkg)
                .user(user)
                .startAt(now)
                .endAt(now.plusDays(pkg.getDurationDays()))
                .build();

        listing.setFeatured(true);
        listingRepository.save(listing);

        return toPromotionResponse(promotionRepository.save(promotion));
    }

    @Transactional(readOnly = true)
    public Page<FeaturedPromotionResponse> getAllPromotions(Pageable pageable) {
        return promotionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toPromotionResponse);
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private FeaturedPackageResponse toPackageResponse(FeaturedPackage pkg) {
        return FeaturedPackageResponse.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .price(pkg.getPrice())
                .durationDays(pkg.getDurationDays())
                .active(pkg.isActive())
                .sortOrder(pkg.getSortOrder())
                .build();
    }

    private FeaturedPromotionResponse toPromotionResponse(FeaturedPromotion p) {
        return FeaturedPromotionResponse.builder()
                .id(p.getId())
                .listingId(p.getListing().getId())
                .listingTitle(p.getListing().getTitle())
                .packageId(p.getFeaturedPackage().getId())
                .packageName(p.getFeaturedPackage().getName())
                .userId(p.getUser().getId())
                .userName(p.getUser().getFullName())
                .startAt(p.getStartAt())
                .endAt(p.getEndAt())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
