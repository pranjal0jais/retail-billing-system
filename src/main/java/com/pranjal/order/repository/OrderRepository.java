package com.pranjal.order.repository;

import com.pranjal.order.OrderStatus;
import com.pranjal.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByOrderStatus(OrderStatus orderStatus);

    List<OrderEntity> findAllByCustomer_Id(Long id);

    List<OrderEntity> findAllByCreatedBy_Id(Long id);

    Integer countAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
