package com.pranjal.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryLogEntity, Long> {
    Page<InventoryLogEntity> findAllByProduct_IdOrderByCreatedAtDesc(Long id,
                                                                     Pageable pageable);
}
