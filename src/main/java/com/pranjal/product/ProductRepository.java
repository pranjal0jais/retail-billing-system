package com.pranjal.product;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsBySku(String sku);

    Page<ProductEntity> findAllByIsActiveIsTrue(Pageable pageable);

    List<ProductEntity> findAllByNameIsContainingIgnoreCaseAndIsActiveIsTrue(String name);

    List<ProductEntity> findAllByStockQuantityLessThanEqualAndIsActiveIsTrue(Integer quantity);

    Optional<ProductEntity> findBySkuAndIsActiveTrue(String sku);

    Optional<ProductEntity> findByIdAndIsActiveIsTrue(Long id);

    boolean existsByCategoryIdAndIsActiveIsTrue(Long categoryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithLock(@Param("id") Long id);
}
