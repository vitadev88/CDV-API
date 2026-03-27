package com.congdongviet.listing.handler;

import com.congdongviet.category.entity.CategoryDetailType;
import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.listing.dto.CarDetailRequest;
import com.congdongviet.listing.dto.CarDetailResponse;
import com.congdongviet.listing.dto.CreateListingRequest;
import com.congdongviet.listing.dto.ListingResponse;
import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.entity.ListingCarDetail;
import com.congdongviet.listing.repository.ListingCarDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarDetailHandler implements ListingDetailHandler {

    private final ListingCarDetailRepository repository;

    @Override
    public CategoryDetailType supports() {
        return CategoryDetailType.CAR;
    }

    @Override
    public void saveDetail(Listing listing, CreateListingRequest request) {
        CarDetailRequest d = request.getCarDetails();
        if (d == null) {
            throw new BadRequestException("Car details are required for this category");
        }

        ListingCarDetail detail = ListingCarDetail.builder()
                .listing(listing)
                .brand(d.getBrand())
                .model(d.getModel())
                .manufactureYear(d.getManufactureYear())
                .mileageKm(d.getMileageKm())
                .fuelType(d.getFuelType())
                .transmission(d.getTransmission())
                .color(d.getColor())
                .build();

        repository.save(detail);
    }

    @Override
    public void populateResponse(Listing listing, ListingResponse.ListingResponseBuilder builder) {
        if (listing.getCarDetail() == null) return;

        ListingCarDetail d = listing.getCarDetail();
        builder.carDetails(CarDetailResponse.builder()
                .brand(d.getBrand())
                .model(d.getModel())
                .manufactureYear(d.getManufactureYear())
                .mileageKm(d.getMileageKm())
                .fuelType(d.getFuelType())
                .transmission(d.getTransmission())
                .color(d.getColor())
                .build());
    }
}
