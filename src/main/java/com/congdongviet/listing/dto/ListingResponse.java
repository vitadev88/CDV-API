package com.congdongviet.listing.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ListingResponse {

    private UUID id;
    private String categoryCode;
    private String categoryName;
    private String title;
    private String description;
    private BigDecimal price;
    private String priceUnit;
    private String status;
    private boolean featured;
    private List<String> imageUrls;
    private String coverImageUrl;
    private UUID createdById;
    private String createdByName;
    private LocalDateTime createdAt;

    /** Populated khi category detailType = REAL_ESTATE */
    private RealEstateDetailResponse realEstateDetails;

    /** Populated khi category detailType = CAR */
    private CarDetailResponse carDetails;

    /** Populated khi category detailType = JOB */
    private JobDetailResponse jobDetails;

    /** Populated khi category detailType = SERVICE */
    private ServiceDetailResponse serviceDetails;

    /** Populated khi category detailType = GOODS */
    private GoodsDetailResponse goodsDetails;
}
