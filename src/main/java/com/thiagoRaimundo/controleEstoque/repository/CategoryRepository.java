package com.thiagoRaimundo.controleEstoque.repository;

import com.thiagoRaimundo.controleEstoque.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    List<Category> findByStatusTrue();

    Optional<Category> findByIdAndStatusTrue(Long id);


    Optional<Category> findByNameAndStatusTrue(String name);


    boolean existsByNameAndStatusTrue(String name);

    List<Category> findByStatusTrueOrderByNameAsc();

    List<Category> findByStatusTrueAndProductsIsNotEmpty();

}
