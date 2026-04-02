package com.congdongviet.listing.handler;

import com.congdongviet.listing.dto.CreateListingRequest;
import com.congdongviet.listing.dto.ListingResponse;
import com.congdongviet.listing.entity.Listing;

/**
 * Strategy interface để xử lý detail của từng loại danh mục.
 *
 * Để thêm danh mục mới (ví dụ: xe máy, điện tử...):
 *   1. Tạo migration: bảng listing_xxx_details
 *   2. Tạo entity ListingXxxDetail
 *   3. Tạo DTO XxxDetailRequest + XxxDetailResponse
 *   4. Implement ListingDetailHandler mới với supports() trả về code tương ứng
 *   5. Thêm loại danh mục vào DB qua API /category-detail-types
 *
 * Không cần sửa ListingService hay bất kỳ code hiện có nào.
 */
public interface ListingDetailHandler {

    /** Trả về code của detailType mà handler này xử lý, ví dụ: "REAL_ESTATE", "CAR" */
    String supports();

    /** Lưu detail vào DB sau khi listing đã được save */
    void saveDetail(Listing listing, CreateListingRequest request);

    /** Map detail vào response DTO */
    void populateResponse(Listing listing, ListingResponse.ListingResponseBuilder builder);
}
