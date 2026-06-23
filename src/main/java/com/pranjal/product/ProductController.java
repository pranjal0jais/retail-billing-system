package com.pranjal.product;

import com.pranjal.common.ApiResponse;
import com.pranjal.product.dto.CreateProductRequest;
import com.pranjal.product.dto.ProductResponse;
import com.pranjal.product.dto.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalogue and stock management")
public class ProductController {

    private final ProductService productService;


    @Operation(summary = "Create a new product with initial stock")
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody @Valid CreateProductRequest request,
                                                        @AuthenticationPrincipal Jwt jwt) {

        Long userId = jwt.getClaim("userId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully",
                        productService.createProduct(request, userId)));
    }

    @Operation(summary = "List all active products")
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(productService.getAllProduct(pageable))
        );
    }

    @Operation(summary = "Search products by name")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByName(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(productService.getAllByName(name))
        );
    }

    @Operation(summary = "Get product by SKU")
    @GetMapping("/sku/{sku}")
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySku(
            @PathVariable String sku
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(productService.getBySku(sku))
        );
    }

    @Operation(summary = "Get a product by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(productService.getProductById(id)));
    }

    @Operation(summary = "Update a product's details or price")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@RequestBody @Valid UpdateProductRequest request,
                                                        @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Product updated successfully",
                        productService.updateProduct(request, id)));
    }

    @Operation(summary = "Soft-delete a product by ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Product deleted successfully"));
    }

    @Operation(summary = "List all products below the low-stock threshold")
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStock(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(productService.getLowStockProducts()));
    }
}
