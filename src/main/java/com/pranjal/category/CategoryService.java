package com.pranjal.category;

import com.pranjal.category.dto.CategoryRequest;
import com.pranjal.category.dto.CategoryResponse;
import com.pranjal.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }

        CategoryEntity category = CategoryEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .build();

        category = categoryRepository.save(category);

        return toResponse(category);
    }

    @Cacheable("categories")
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByIsActiveIsTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse updateCategory(CategoryRequest request, Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Category not found")
                );

        if (categoryRepository.existsByName(request.getName())
                && !category.getName().equals(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);

        return toResponse(category);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        CategoryEntity category = categoryRepository.findByIdAndIsActiveIsTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Category not found")
                );

        if(productRepository.existsByCategoryIdAndIsActiveIsTrue(id)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category has active products");
        }

        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(CategoryEntity entity) {
        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
