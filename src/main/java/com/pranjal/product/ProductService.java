package com.pranjal.product;

import com.pranjal.category.CategoryEntity;
import com.pranjal.category.CategoryRepository;
import com.pranjal.inventory.ChangeType;
import com.pranjal.inventory.InventoryLogEntity;
import com.pranjal.inventory.InventoryRepository;
import com.pranjal.product.dto.CreateProductRequest;
import com.pranjal.product.dto.ProductResponse;
import com.pranjal.product.dto.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    @Value("${app.low-stock-threshold}")
    private Integer lowStockLimit;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, Long userId) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT
                    , "Product with SKU already exists");
        }

        CategoryEntity category =
                categoryRepository.findByIdAndIsActiveIsTrue(request.getCategoryId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Category not found"));

        ProductEntity product = ProductEntity.builder()
                .name(request.getName())
                .category(category)
                .price(request.getPrice())
                .sku(request.getSku())
                .stockQuantity(request.getStockQuantity())
                .build();

        product = productRepository.save(product);

        InventoryLogEntity inventoryLog = InventoryLogEntity.builder()
                .product(product)
                .changeType(ChangeType.MANUAL_ADD)
                .quantityChanged(product.getStockQuantity())
                .quantityAfter(product.getStockQuantity())
                .referenceId(null)
                .note("Initial stock on product creation")
                .createdBy(userId)
                .build();

        inventoryRepository.save(inventoryLog);

        return toResponse(product);
    }

    public Page<ProductResponse> getAllProduct(Pageable pageable) {
        return productRepository.findAllByIsActiveIsTrue(pageable).map(this::toResponse);
    }

    public List<ProductResponse> getAllByName(String name) {
        return productRepository.findAllByNameIsContainingIgnoreCaseAndIsActiveIsTrue(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getBySku(String sku) {
        ProductEntity product = productRepository.findBySkuAndIsActiveTrue(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                        , "Product not found"));

        return toResponse(product);
    }

    public ProductResponse getProductById(Long id) {
        ProductEntity product = productRepository.findByIdAndIsActiveIsTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                        , "Product not found"));

        return toResponse(product);
    }

    public ProductResponse updateProduct(UpdateProductRequest request, Long productId) {
        ProductEntity product = productRepository.findByIdAndIsActiveIsTrue(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                        , "Product not found"));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        if (!Objects.equals(request.getCategoryId(), product.getCategory().getId())) {
            CategoryEntity category = categoryRepository.findByIdAndIsActiveIsTrue(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Category not found")
                    );
            product.setCategory(category);
        }

        productRepository.save(product);
        return toResponse(product);
    }

    public void deleteProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                        , "Product not found"));

        product.setIsActive(false);
        productRepository.save(product);
    }

    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findAllByStockQuantityLessThanEqualAndIsActiveIsTrue(lowStockLimit)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductResponse toResponse(ProductEntity entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .sku(entity.getSku())
                .isActive(entity.getIsActive())
                .categoryId(entity.getCategory().getId())
                .categoryName(entity.getCategory().getName())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
