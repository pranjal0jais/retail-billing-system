package com.pranjal.report;

import com.pranjal.order.OrderStatus;
import com.pranjal.order.repository.OrderItemRepository;
import com.pranjal.order.repository.OrderRepository;
import com.pranjal.report.dto.DailySalesResponse;
import com.pranjal.report.dto.SalesSummaryResponse;
import com.pranjal.report.dto.TopSellingProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public SalesSummaryResponse getSalesSummary(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalRevenue = orderRepository.getTotalRevenue(startDate, endDate);
        Long totalOrders = orderRepository.countByOrderStatusAndCreatedAtBetween(OrderStatus.PAID,
                startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        BigDecimal averageOrderRevenue = totalOrders == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);

        return SalesSummaryResponse.builder()
                .totalRevenue(totalRevenue)
                .orderCount(totalOrders)
                .averageOrderValue(averageOrderRevenue)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public List<DailySalesResponse> getDailySales(LocalDate startDate, LocalDate endDate) {
        List<Object[]> objects = new ArrayList<>(orderRepository.getDailySales(startDate, endDate));
        return objects
                .stream()
                .map(this::toDailySalesResponse)
                .toList();
    }

    public List<TopSellingProductResponse> getTopSellingProducts(LocalDate startDate,
                                                                 LocalDate endDate,
                                                                 int limit) {
        List<Object[]> objects = orderItemRepository.getTopSellingProducts(startDate, endDate,
                PageRequest.of(0, limit));
        return objects
                .stream()
                .map(this::toTopSellingProductResponse)
                .toList();
    }

    private TopSellingProductResponse toTopSellingProductResponse(Object[] row) {
        return TopSellingProductResponse.builder()
                .productId((Long) row[0])
                .productName(row[1].toString())
                .totalQuantitySold((Long) row[2])
                .totalRevenue((BigDecimal) row[3])
                .build();
    }

    private DailySalesResponse toDailySalesResponse(Object[] row) {
        return DailySalesResponse.builder()
                .date(((java.sql.Date) row[0]).toLocalDate())
                .totalRevenue((BigDecimal) row[1])
                .orderCount((Long) row[2])
                .build();
    }
}
