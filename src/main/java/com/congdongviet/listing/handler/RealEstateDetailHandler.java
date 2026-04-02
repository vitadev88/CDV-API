package com.congdongviet.listing.handler;

import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.listing.dto.CreateListingRequest;
import com.congdongviet.listing.dto.ListingResponse;
import com.congdongviet.listing.dto.RealEstateDetailRequest;
import com.congdongviet.listing.dto.RealEstateDetailResponse;
import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.entity.ListingRealEstateDetail;
import com.congdongviet.listing.repository.ListingRealEstateDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RealEstateDetailHandler implements ListingDetailHandler {

    private final ListingRealEstateDetailRepository repository;

    @Override
    public String supports() {
        return "REAL_ESTATE";
    }

    @Override
    public void saveDetail(Listing listing, CreateListingRequest request) {
        RealEstateDetailRequest d = request.getRealEstateDetails();
        if (d == null) {
            throw new BadRequestException("Real estate details are required for this category");
        }

        ListingRealEstateDetail detail = ListingRealEstateDetail.builder()
                .listing(listing)
                .city(d.getCity())
                .district(d.getDistrict())
                .address(d.getAddress())
                .areaSqm(d.getAreaSqm())
                .bedrooms(d.getBedrooms())
                .bathrooms(d.getBathrooms())
                .floors(d.getFloors())
                .furnished(d.isFurnished())
                .availableFrom(d.getAvailableFrom())
                .deposit(d.getDeposit())
                .build();

        repository.save(detail);
    }

    @Override
    public void populateResponse(Listing listing, ListingResponse.ListingResponseBuilder builder) {
        if (listing.getRealEstateDetail() == null) return;

        ListingRealEstateDetail d = listing.getRealEstateDetail();
        builder.realEstateDetails(RealEstateDetailResponse.builder()
                .city(d.getCity())
                .district(d.getDistrict())
                .address(d.getAddress())
                .areaSqm(d.getAreaSqm())
                .bedrooms(d.getBedrooms())
                .bathrooms(d.getBathrooms())
                .floors(d.getFloors())
                .furnished(d.isFurnished())
                .availableFrom(d.getAvailableFrom())
                .deposit(d.getDeposit())
                .build());
    }
}
