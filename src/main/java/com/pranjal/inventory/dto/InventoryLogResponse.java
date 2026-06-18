package com.pranjal.inventory.dto;

import com.pranjal.inventory.ChangeType;
import com.pranjal.product.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryLogResponse {
    private Long id;
    private Long productId;
    private String productName;
    private ChangeType changeType;
    private Integer quantityChanged;
    private Integer quantityAfter;
    private Long referenceId;
    private String note;
    private Long createdBy;
    private LocalDateTime createdAt;
}
