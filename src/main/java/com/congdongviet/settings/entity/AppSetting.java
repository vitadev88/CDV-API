package com.congdongviet.settings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_settings")
@Getter
@Setter
@NoArgsConstructor
public class AppSetting {

    @Id
    private Long id = 1L;

    @Column(name = "show_featured", nullable = false)
    private boolean showFeatured = true;

    @Column(name = "show_featured_packages", nullable = false)
    private boolean showFeaturedPackages = true;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "site_title", nullable = false, length = 255)
    private String siteTitle = "CongDongViet - Cộng đồng Việt Nam tại Đài Loan";

    @Column(name = "site_description", columnDefinition = "TEXT")
    private String siteDescription;

    @Column(name = "maintenance_mode", nullable = false)
    private boolean maintenanceMode = false;
}
