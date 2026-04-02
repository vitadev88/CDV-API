package com.congdongviet.listing.service;

import com.congdongviet.auth.entity.User;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.category.entity.Category;
import com.congdongviet.category.repository.CategoryRepository;
import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.common.exception.ResourceNotFoundException;
import com.congdongviet.listing.dto.CreateListingRequest;
import com.congdongviet.listing.dto.ListingResponse;
import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.entity.ListingImage;
import com.congdongviet.listing.entity.ListingStatus;
import com.congdongviet.listing.entity.PriceUnit;
import com.congdongviet.listing.handler.ListingDetailHandler;
import com.congdongviet.listing.repository.ListingRepository;
import com.congdongviet.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * Map từ detailType code → handler, được inject tự động qua Spring.
     * Khi thêm handler mới, Spring tự đăng ký — không cần sửa service này.
     */
    private final Map<String, ListingDetailHandler> handlers;

    public ListingService(ListingRepository listingRepository,
                          CategoryRepository categoryRepository,
                          UserRepository userRepository,
                          List<ListingDetailHandler> handlerList) {
        this.listingRepository = listingRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(ListingDetailHandler::supports, Function.identity()));
    }

    @Transactional
    public ListingResponse create(CreateListingRequest request, UserPrincipal currentUser) {
        Category category = categoryRepository.findByCode(request.getCategoryCode())
                .orElseThrow(() -> new BadRequestException("Invalid category: " + request.getCategoryCode()));

        if (!category.isActive()) {
            throw new BadRequestException("Category is not active");
        }
        if (category.getDetailType() == null) {
            throw new BadRequestException("Cannot post listing directly under a parent category");
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        Listing listing = Listing.builder()
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .priceUnit(PriceUnit.valueOf(request.getPriceUnit()))
                .status(ListingStatus.ACTIVE)
                .createdBy(user)
                .build();

        listingRepository.save(listing);

        // Lưu ảnh nếu có
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<String> urls = request.getImageUrls();
            for (int i = 0; i < urls.size(); i++) {
                listing.getImages().add(ListingImage.builder()
                        .listing(listing)
                        .url(urls.get(i))
                        .cover(i == 0)
                        .sortOrder(i)
                        .build());
            }
            listingRepository.save(listing);
        }

        ListingDetailHandler handler = handlers.get(category.getDetailType().getCode());
        if (handler != null) {
            handler.saveDetail(listing, request);
        }

        return toResponse(listing);
    }

    @Transactional(readOnly = true)
    public ListingResponse getById(UUID id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", id));
        return toResponse(listing);
    }

    @Transactional(readOnly = true)
    public Page<ListingResponse> getAll(Pageable pageable) {
        return listingRepository.findByStatus(ListingStatus.ACTIVE, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ListingResponse> getByCategoryCode(String categoryCode, Pageable pageable) {
        Category category = categoryRepository.findByCode(categoryCode)
                .orElseThrow(() -> new BadRequestException("Invalid category: " + categoryCode));
        return listingRepository
                .findByCategoryIdAndStatus(category.getId(), ListingStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    /** Tìm kiếm nâng cao với nhiều filter */
    @Transactional(readOnly = true)
    public Page<ListingResponse> search(String categoryCode, String keyword,
                                         java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
                                         String city, Double minArea, Double maxArea,
                                         Pageable pageable) {
        Specification<Listing> spec = Specification.where(
                (root, q, cb) -> cb.equal(root.get("status"), ListingStatus.ACTIVE));

        if (categoryCode != null && !categoryCode.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.join("category").get("code"), categoryCode.toUpperCase()));
        }
        if (keyword != null && !keyword.isBlank()) {
            String kw = "%" + keyword.toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), kw),
                    cb.like(cb.lower(root.get("description")), kw)));
        }
        if (minPrice != null) {
            spec = spec.and((root, q, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, q, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        if (city != null && !city.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(cb.lower(root.join("realEstateDetail").get("city")), city.toLowerCase()));
        }
        if (minArea != null) {
            spec = spec.and((root, q, cb) ->
                    cb.greaterThanOrEqualTo(root.join("realEstateDetail").get("areaSqm"), minArea));
        }
        if (maxArea != null) {
            spec = spec.and((root, q, cb) ->
                    cb.lessThanOrEqualTo(root.join("realEstateDetail").get("areaSqm"), maxArea));
        }

        return listingRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional
    public void updateStatus(UUID listingId, ListingStatus newStatus, UserPrincipal currentUser) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", listingId));

        if (!listing.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You are not the owner of this listing");
        }

        listing.setStatus(newStatus);
        listingRepository.save(listing);
    }

    @Transactional
    public void setFeatured(UUID listingId, boolean featured) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", listingId));
        listing.setFeatured(featured);
        listingRepository.save(listing);
    }

    @Transactional(readOnly = true)
    public Page<ListingResponse> getFeatured(Pageable pageable) {
        return listingRepository.findByFeaturedAndStatus(true, ListingStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    /** Admin: lấy tất cả listing mọi trạng thái, hỗ trợ filter */
    @Transactional(readOnly = true)
    public Page<ListingResponse> getAllAdmin(String keyword, String status, String categoryCode, Boolean featured, Pageable pageable) {
        Specification<Listing> spec = Specification.where(null);
        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.get("status"), ListingStatus.valueOf(status.toUpperCase())));
        }
        if (categoryCode != null && !categoryCode.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.join("category").get("code"), categoryCode.toUpperCase()));
        }
        if (featured != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("featured"), featured));
        }
        return listingRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ListingResponse> getMyListings(UUID userId, Pageable pageable) {
        return listingRepository.findByCreatedByIdAndStatus(userId, ListingStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ListingResponse> getUserListings(UUID userId, Pageable pageable) {
        return listingRepository.findByCreatedByIdAndStatus(userId, ListingStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    /** Admin: xoá cứng listing */
    @Transactional
    public void delete(UUID id) {
        if (!listingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Listing", "id", id);
        }
        listingRepository.deleteById(id);
    }

    private ListingResponse toResponse(Listing listing) {
        List<String> imageUrls = listing.getImages().stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(img -> img.getUrl())
                .toList();

        String coverUrl = listing.getImages().stream()
                .filter(img -> img.isCover())
                .map(img -> img.getUrl())
                .findFirst()
                .orElse(imageUrls.isEmpty() ? null : imageUrls.get(0));

        ListingResponse.ListingResponseBuilder builder = ListingResponse.builder()
                .id(listing.getId())
                .categoryCode(listing.getCategory().getCode())
                .categoryName(listing.getCategory().getName())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .price(listing.getPrice())
                .priceUnit(listing.getPriceUnit().name())
                .status(listing.getStatus().name())
                .featured(listing.isFeatured())
                .imageUrls(imageUrls)
                .coverImageUrl(coverUrl)
                .createdById(listing.getCreatedBy().getId())
                .createdByName(listing.getCreatedBy().getFullName())
                .createdAt(listing.getCreatedAt());

        var detailType = listing.getCategory().getDetailType();
        if (detailType != null) {
            ListingDetailHandler handler = handlers.get(detailType.getCode());
            if (handler != null) {
                handler.populateResponse(listing, builder);
            }
        }

        return builder.build();
    }
}
