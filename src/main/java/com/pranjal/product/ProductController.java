package com.pranjal.product;

import com.pranjal.common.ApiResponse;
import com.pranjal.product.dto.CreateProductRequest;
import com.pranjal.product.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> createProduct(@RequestBody @Valid CreateProductRequest request,
                                                        @AuthenticationPrincipal Jwt jwt) {

        Long userId = jwt.getClaim("userId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully",
                        productService.createProduct(request, userId)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<?>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku
    ) {
        if (name == null && sku == null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(productService
                            .getAllProduct()));
        }

        if (sku != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(productService
                            .getBySku(sku)));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(productService
                        .getAllByName(name)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<?>> getProductById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(productService.getProductById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> updateProduct(@RequestBody @Valid UpdateProductRequest request,
                                                        @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Product updated successfully",
                        productService.updateProduct(request, id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Product deleted successfully"));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> getLowStock(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(productService.getLowStockProducts()));
    }
}
