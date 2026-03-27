package com.congdongviet.listing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateListingRequest {

    /**
     * Code của danh mục lá (ví dụ: HOUSE_RENT, CAR_SALE).
     * Frontend lấy danh sách từ GET /api/v1/categories
     */
    @NotBlank(message = "Category code is required")
    private String categoryCode;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    /** MONTHLY | TOTAL | NEGOTIABLE */
    @NotBlank(message = "Price unit is required")
    private String priceUnit;

    /** Điền nếu categoryCode thuộc nhóm BĐS */
    @Valid
    private RealEstateDetailRequest realEstateDetails;

    /** Điền nếu categoryCode là CAR_SALE */
    @Valid
    private CarDetailRequest carDetails;
}
