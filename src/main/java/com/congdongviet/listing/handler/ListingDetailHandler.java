package com.congdongviet.listing.handler;

import com.congdongviet.category.entity.CategoryDetailType;
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
 *   4. Implement ListingDetailHandler mới
 *   5. Thêm danh mục vào DB (qua migration seed)
 *
 * Không cần sửa ListingService hay bất kỳ code hiện có nào.
 */
public interface ListingDetailHandler {

    /** Handler này xử lý category có detailType tương ứng */
    CategoryDetailType supports();

    /** Lưu detail vào DB sau khi listing đã được save */
    void saveDetail(Listing listing, CreateListingRequest request);

    /** Map detail vào response DTO */
    void populateResponse(Listing listing, ListingResponse.ListingResponseBuilder builder);
}
