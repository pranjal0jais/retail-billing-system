package com.pranjal.order.repository;

import com.pranjal.order.entity.OrderItemEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findAllByOrder_Id(Long id);

    Optional<OrderItemEntity> findByIdAndOrder_Id(Long itemId, Long orderId);

    @Query("SELECT oi.product.id, oi.productName, SUM(oi.quantity), SUM(oi.lineTotal) " +
            "FROM OrderItemEntity oi JOIN oi.order o " +
            "WHERE o.orderStatus = 'PAID' AND DATE(o.createdAt) BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.product.id, oi.productName " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getTopSellingProducts(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         PageRequest pageable);
}
