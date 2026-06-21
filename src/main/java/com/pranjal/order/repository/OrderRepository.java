package com.pranjal.order.repository;

import com.pranjal.order.OrderStatus;
import com.pranjal.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByOrderStatus(OrderStatus orderStatus);

    List<OrderEntity> findAllByCustomer_Id(Long id);

    List<OrderEntity> findAllByCreatedBy_Id(Long id);

    Integer countAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM OrderEntity o WHERE o.orderStatus = 'PAID' " +
            "AND DATE(o.createdAt) BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenue(@Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    Long countByOrderStatusAndCreatedAtBetween(OrderStatus status,
                                               LocalDateTime start,
                                               LocalDateTime end);

    @Query("SELECT DATE(o.createdAt), SUM(o.totalAmount), COUNT(o) FROM OrderEntity o " +
            "WHERE o.orderStatus = 'PAID' AND DATE(o.createdAt) BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.createdAt) ORDER BY DATE(o.createdAt)")
    List<Object[]> getDailySales(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);
}
