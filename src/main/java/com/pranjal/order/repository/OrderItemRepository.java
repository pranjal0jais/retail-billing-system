package com.pranjal.order.repository;

import com.pranjal.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findAllByOrder_Id(Long id);

    Optional<OrderItemEntity> findByIdAndOrder_Id(Long itemId, Long orderId);
}
