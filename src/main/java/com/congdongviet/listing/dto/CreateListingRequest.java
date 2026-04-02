package com.congdongviet.listing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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

    /** Danh sách URL ảnh (upload trước qua POST /api/v1/uploads). Ảnh đầu tiên là ảnh bìa. */
    private List<String> imageUrls;

    /** Điền nếu categoryCode thuộc nhóm BĐS */
    @Valid
    private RealEstateDetailRequest realEstateDetails;

    /** Điền nếu categoryCode thuộc nhóm xe cộ */
    @Valid
    private CarDetailRequest carDetails;

    /** Điền nếu categoryCode thuộc nhóm việc làm */
    @Valid
    private JobDetailRequest jobDetails;

    /** Điền nếu categoryCode thuộc nhóm dịch vụ */
    @Valid
    private ServiceDetailRequest serviceDetails;

    /** Điền nếu categoryCode thuộc nhóm mua bán đồ dùng */
    @Valid
    private GoodsDetailRequest goodsDetails;
}
