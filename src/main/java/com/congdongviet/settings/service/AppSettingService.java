package com.congdongviet.settings.service;

import com.congdongviet.settings.dto.AppSettingDto;
import com.congdongviet.settings.entity.AppSetting;
import com.congdongviet.settings.repository.AppSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppSettingService {

    private final AppSettingRepository repository;

    public AppSettingDto get() {
        AppSetting s = repository.findById(1L).orElseGet(AppSetting::new);
        return new AppSettingDto(s.isShowFeatured(), s.isShowFeaturedPackages());
    }

    @Transactional
    public AppSettingDto update(AppSettingDto dto) {
        AppSetting s = repository.findById(1L).orElseGet(AppSetting::new);
        s.setShowFeatured(dto.isShowFeatured());
        s.setShowFeaturedPackages(dto.isShowFeaturedPackages());
        repository.save(s);
        return dto;
    }
}
