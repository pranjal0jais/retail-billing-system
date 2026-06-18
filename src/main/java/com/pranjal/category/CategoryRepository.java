package com.pranjal.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findAllByIsActiveIsTrue();

    boolean existsByName(String name);

    Optional<CategoryEntity> findByIdAndIsActiveIsTrue(Long id);
}
