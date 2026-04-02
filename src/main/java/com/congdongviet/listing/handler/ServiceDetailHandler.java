package com.congdongviet.listing.handler;

import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.listing.dto.CreateListingRequest;
import com.congdongviet.listing.dto.ListingResponse;
import com.congdongviet.listing.dto.ServiceDetailRequest;
import com.congdongviet.listing.dto.ServiceDetailResponse;
import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.entity.ListingServiceDetail;
import com.congdongviet.listing.repository.ListingServiceDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceDetailHandler implements ListingDetailHandler {

    private final ListingServiceDetailRepository repository;

    @Override
    public String supports() {
        return "SERVICE";
    }

    @Override
    public void saveDetail(Listing listing, CreateListingRequest request) {
        ServiceDetailRequest d = request.getServiceDetails();
        if (d == null) {
            throw new BadRequestException("Service details are required for this category");
        }

        ListingServiceDetail detail = ListingServiceDetail.builder()
                .listing(listing)
                .city(d.getCity())
                .district(d.getDistrict())
                .contactName(d.getContactName())
                .contactPhone(d.getContactPhone())
                .build();

        repository.save(detail);
    }

    @Override
    public void populateResponse(Listing listing, ListingResponse.ListingResponseBuilder builder) {
        if (listing.getServiceDetail() == null) return;

        ListingServiceDetail d = listing.getServiceDetail();
        builder.serviceDetails(ServiceDetailResponse.builder()
                .city(d.getCity())
                .district(d.getDistrict())
                .contactName(d.getContactName())
                .contactPhone(d.getContactPhone())
                .build());
    }
}
