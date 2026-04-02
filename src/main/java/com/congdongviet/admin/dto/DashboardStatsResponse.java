package com.congdongviet.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardStatsResponse {

    private long totalListings;
    private long totalUsers;
    private long featuredListings;
    private long totalCategories;

    /** Số tin đăng mới tháng này */
    private long newListingsThisMonth;

    /** Số người dùng mới tháng này */
    private long newUsersThisMonth;

    /** 12 tháng gần nhất — tin đăng */
    private List<MonthStat> listingsByMonth;

    /** 12 tháng gần nhất — người dùng mới */
    private List<MonthStat> usersByMonth;

    /** Top 6 danh mục theo số tin */
    private List<CategoryStat> topCategories;

    @Getter
    @Builder
    public static class MonthStat {
        private String label;  // "T1", "T2" ... "T12"
        private long count;
    }

    @Getter
    @Builder
    public static class CategoryStat {
        private String name;
        private long count;
    }
}
