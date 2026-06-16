package com.pranjal.category;


import com.pranjal.category.dto.CategoryRequest;
import com.pranjal.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> createCategory(@RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("New category added successfully",
                        categoryService.createCategory(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
    public ResponseEntity<ApiResponse<?>> getAllCategory() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        categoryService.getAllCategories()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> updateCategory(@PathVariable Long id,
                                                        @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Category updated successfully",
                        categoryService.updateCategory(request, id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success("Category deleted successfully"));
    }
}
