package com.congdongviet.admin.service;

import com.congdongviet.admin.dto.DashboardStatsResponse;
import com.congdongviet.auth.repository.UserRepository;
import com.congdongviet.category.repository.CategoryRepository;
import com.congdongviet.listing.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusMonths(11).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime thisMonthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        // ── Totals ────────────────────────────────────────────────────────
        long totalListings   = listingRepository.count();
        long totalUsers      = userRepository.count();
        long featuredCount   = listingRepository.countByFeaturedTrue();
        long totalCategories = categoryRepository.countByActiveTrue();

        // ── This month ────────────────────────────────────────────────────
        long newListingsThisMonth = listingRepository.countByMonth(thisMonthStart)
                .stream().mapToLong(r -> ((Number) r[2]).longValue()).sum();
        long newUsersThisMonth = userRepository.countByMonth(thisMonthStart)
                .stream().mapToLong(r -> ((Number) r[2]).longValue()).sum();

        // ── Listings by month (last 12) ───────────────────────────────────
        List<DashboardStatsResponse.MonthStat> listingsByMonth =
                buildMonthStats(listingRepository.countByMonth(oneYearAgo));

        // ── Users by month (last 12) ──────────────────────────────────────
        List<DashboardStatsResponse.MonthStat> usersByMonth =
                buildMonthStats(userRepository.countByMonth(oneYearAgo));

        // ── Top categories ────────────────────────────────────────────────
        List<DashboardStatsResponse.CategoryStat> topCategories = listingRepository
                .countByCategory(PageRequest.of(0, 6))
                .stream()
                .map(r -> DashboardStatsResponse.CategoryStat.builder()
                        .name((String) r[0])
                        .count(((Number) r[1]).longValue())
                        .build())
                .toList();

        return DashboardStatsResponse.builder()
                .totalListings(totalListings)
                .totalUsers(totalUsers)
                .featuredListings(featuredCount)
                .totalCategories(totalCategories)
                .newListingsThisMonth(newListingsThisMonth)
                .newUsersThisMonth(newUsersThisMonth)
                .listingsByMonth(listingsByMonth)
                .usersByMonth(usersByMonth)
                .topCategories(topCategories)
                .build();
    }

    /** Điền đủ 12 tháng, tháng không có data = 0 */
    private List<DashboardStatsResponse.MonthStat> buildMonthStats(List<Object[]> rows) {
        Map<String, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            int year  = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long cnt  = ((Number) row[2]).longValue();
            map.put(year + "-" + month, cnt);
        }

        List<DashboardStatsResponse.MonthStat> result = new ArrayList<>();
        LocalDateTime cursor = LocalDateTime.now().minusMonths(11).withDayOfMonth(1);
        for (int i = 0; i < 12; i++) {
            int year  = cursor.getYear();
            int month = cursor.getMonthValue();
            long cnt  = map.getOrDefault(year + "-" + month, 0L);
            result.add(DashboardStatsResponse.MonthStat.builder()
                    .label("T" + month)
                    .count(cnt)
                    .build());
            cursor = cursor.plusMonths(1);
        }
        return result;
    }
}
