package com.congdongviet.listing.handler;

import com.congdongviet.common.exception.BadRequestException;
import com.congdongviet.listing.dto.CreateListingRequest;
import com.congdongviet.listing.dto.JobDetailRequest;
import com.congdongviet.listing.dto.JobDetailResponse;
import com.congdongviet.listing.dto.ListingResponse;
import com.congdongviet.listing.entity.JobType;
import com.congdongviet.listing.entity.Listing;
import com.congdongviet.listing.entity.ListingJobDetail;
import com.congdongviet.listing.repository.ListingJobDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobDetailHandler implements ListingDetailHandler {

    private final ListingJobDetailRepository repository;

    @Override
    public String supports() {
        return "JOB";
    }

    @Override
    public void saveDetail(Listing listing, CreateListingRequest request) {
        JobDetailRequest d = request.getJobDetails();
        if (d == null) {
            throw new BadRequestException("Job details are required for this category");
        }

        ListingJobDetail detail = ListingJobDetail.builder()
                .listing(listing)
                .jobType(d.getJobType() != null ? JobType.valueOf(d.getJobType()) : null)
                .city(d.getCity())
                .district(d.getDistrict())
                .salaryMin(d.getSalaryMin())
                .salaryMax(d.getSalaryMax())
                .experienceYears(d.getExperienceYears())
                .contactName(d.getContactName())
                .build();

        repository.save(detail);
    }

    @Override
    public void populateResponse(Listing listing, ListingResponse.ListingResponseBuilder builder) {
        if (listing.getJobDetail() == null) return;

        ListingJobDetail d = listing.getJobDetail();
        builder.jobDetails(JobDetailResponse.builder()
                .jobType(d.getJobType() != null ? d.getJobType().name() : null)
                .city(d.getCity())
                .district(d.getDistrict())
                .salaryMin(d.getSalaryMin())
                .salaryMax(d.getSalaryMax())
                .experienceYears(d.getExperienceYears())
                .contactName(d.getContactName())
                .build());
    }
}
