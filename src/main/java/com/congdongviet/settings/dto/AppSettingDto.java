package com.congdongviet.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppSettingDto {
    private boolean showFeatured;
    private boolean showFeaturedPackages;
    private String logoUrl;
    private String siteTitle;
    private String siteDescription;
    private boolean maintenanceMode;
}
