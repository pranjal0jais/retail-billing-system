package com.pranjal.inventory;

import com.pranjal.inventory.dto.AdjustStockRequest;
import com.pranjal.inventory.dto.InventoryLogResponse;
import com.pranjal.product.ProductEntity;
import com.pranjal.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @CacheEvict(value = {"products", "lowStocks"}, allEntries = true)
    public InventoryLogResponse adjustStock(AdjustStockRequest request, Long userId) {
        if (request.getQuantityChanged() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity changed cannot be zero");
        }

        ProductEntity product = productRepository.findByIdAndIsActiveIsTrue(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found"));

        int newStock = product.getStockQuantity() + request.getQuantityChanged();
        if(newStock < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Insufficient stock");
        }

        product.setStockQuantity(newStock);
        productRepository.save(product);

        InventoryLogEntity log =
                InventoryLogEntity.builder().product(product)
                        .changeType((request.getQuantityChanged() > 0)
                                ?ChangeType.MANUAL_ADD
                                :ChangeType.MANUAL_REMOVE)
                        .quantityChanged(request.getQuantityChanged())
                        .quantityAfter(newStock)
                        .referenceId(null)
                        .note(request.getNote())
                        .createdBy(userId)
                        .build();

        log = inventoryRepository.save(log);
        return toResponse(log);
    }

    public Page<InventoryLogResponse> getLogs(Pageable pageable) {
        return inventoryRepository.findAll(pageable).map(this::toResponse);
    }

    public Page<InventoryLogResponse> getLogsByProductId(Long productId, Pageable pageable){
        return inventoryRepository.findAllByProduct_IdOrderByCreatedAtDesc(productId, pageable)
                .map(this::toResponse);
    }

    private InventoryLogResponse toResponse(InventoryLogEntity entity){
        return InventoryLogResponse.builder().id(entity.getId())
                .productId(entity.getProduct().getId())
                .productName(entity.getProduct().getName())
                .changeType(entity.getChangeType())
                .quantityChanged(entity.getQuantityChanged())
                .quantityAfter(entity.getQuantityAfter())
                .referenceId(entity.getReferenceId())
                .note(entity.getNote())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}


