package com.pranjal.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsBySku(String sku);

    List<ProductEntity> findAllByIsActiveIsTrue();

    Optional<ProductEntity> findAllBySkuAndIsActiveIsTrue(String sku);

    List<ProductEntity> findAllByNameIsContainingIgnoreCaseAndIsActiveIsTrue(String name);

    List<ProductEntity> findAllByStockQuantityLessThanEqualAndIsActiveIsTrue(Integer quantity);

    Optional<ProductEntity> findBySkuAndIsActiveTrue(String sku);

    Optional<ProductEntity> findByIdAndIsActiveIsTrue(Long id);

    boolean existsByCategoryIdAndIsActiveIsTrue(Long categoryId);
}
