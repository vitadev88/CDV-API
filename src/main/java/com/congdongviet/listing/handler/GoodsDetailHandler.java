package com.congdongviet.listing.handler;

import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.listing.dto.CreateListingRequest;
import com.congdongviet.listing.dto.GoodsDetailRequest;
import com.congdongviet.listing.dto.GoodsDetailResponse;
import com.congdongviet.listing.dto.ListingResponse;
import com.congdongviet.listing.entity.GoodsCondition;
import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.entity.ListingGoodsDetail;
import com.congdongviet.listing.repository.ListingGoodsDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoodsDetailHandler implements ListingDetailHandler {

    private final ListingGoodsDetailRepository repository;

    @Override
    public String supports() {
        return "GOODS";
    }

    @Override
    public void saveDetail(Listing listing, CreateListingRequest request) {
        GoodsDetailRequest d = request.getGoodsDetails();
        if (d == null) {
            throw new BadRequestException("Goods details are required for this category");
        }

        ListingGoodsDetail detail = ListingGoodsDetail.builder()
                .listing(listing)
                .goodsCondition(d.getCondition() != null ? GoodsCondition.valueOf(d.getCondition()) : null)
                .brand(d.getBrand())
                .city(d.getCity())
                .build();

        repository.save(detail);
    }

    @Override
    public void populateResponse(Listing listing, ListingResponse.ListingResponseBuilder builder) {
        if (listing.getGoodsDetail() == null) return;

        ListingGoodsDetail d = listing.getGoodsDetail();
        builder.goodsDetails(GoodsDetailResponse.builder()
                .condition(d.getGoodsCondition() != null ? d.getGoodsCondition().name() : null)
                .brand(d.getBrand())
                .city(d.getCity())
                .build());
    }
}
