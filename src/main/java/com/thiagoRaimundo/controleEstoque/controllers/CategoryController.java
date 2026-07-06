package com.thiagoRaimundo.controleEstoque.controllers;


import com.thiagoRaimundo.controleEstoque.DTOs.CategoryRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.CategoryResponse;
import com.thiagoRaimundo.controleEstoque.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/categories")
public class CategoryController {


    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/nome/{name}")
    public ResponseEntity<CategoryResponse> getCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getCategoryByName(name));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/ordenadas")
    public ResponseEntity<List<CategoryResponse>> getAllCategoriesSorted() {
        return ResponseEntity.ok(categoryService.getAllCategoriesSorted());
    }

    @GetMapping("/{id}/contar-produtos")
    public ResponseEntity<Integer> countProductsByCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.countProductsByCategory(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/forcar")
    public ResponseEntity<Void> deleteCategoryForce(@PathVariable Long id) {
        categoryService.deleteCategoryForce(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existsCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.existsCategory(id));
    }


}
