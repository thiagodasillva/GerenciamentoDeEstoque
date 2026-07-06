package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.CategoryRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.CategoryResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.Category;
import com.thiagoRaimundo.controleEstoque.repository.CategoryRepository;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private ModelMapper modelMapper;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;

    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        // Verificar se já existe categoria com o mesmo nome
        if (categoryRepository.existsByNameAndStatusTrue(categoryRequest.getName())) {
            throw new RuntimeException("Já existe uma categoria com o nome: " + categoryRequest.getName());
        }

        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setStatus(true);

        Category savedCategory = categoryRepository.save(category);
        return entityToDto(savedCategory);
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findByIdAndStatusTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. ID: " + id));
        return entityToDto(category);
    }

    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryRepository.findByNameAndStatusTrue(name)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. Nome: " + name));
        return entityToDto(category);
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByStatusTrue()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getAllCategoriesSorted() {
        return categoryRepository.findByStatusTrueOrderByNameAsc()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findByIdAndStatusTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. ID: " + id));

        if (!category.getName().equals(categoryRequest.getName()) &&
                categoryRepository.existsByNameAndStatusTrue(categoryRequest.getName())) {
            throw new RuntimeException("Já existe uma categoria com o nome: " + categoryRequest.getName());
        }

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return entityToDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndStatusTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. ID: " + id));

        //Verificar se a categoria tem produtos associados
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new RuntimeException("Não é possível excluir a categoria pois ela possui produtos associados. " +
                    "Quantidade de produtos: " + category.getProducts().size());
        }

        category.setStatus(false);
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategoryForce(Long id) {
        Category category = categoryRepository.findByIdAndStatusTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. ID: " + id));

        // Remover todos os produtos associados
        category.getProducts().forEach(product -> {
            product.setStatus(false);
            product.setCategory(null);
        });

        category.setStatus(false);
        categoryRepository.save(category);
    }

    //  Método para verificar se categoria existe
    public boolean existsCategory(Long id) {
        return categoryRepository.existsById(id);
    }

    // Método para contar produtos por categoria
    public Integer countProductsByCategory(Long id) {
        Category category = categoryRepository.findByIdAndStatusTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada. ID: " + id));

        if (category.getProducts() == null) {
            return 0;
        }
        return (int) category.getProducts().stream()
                .filter(product -> product.getStatus() != null && product.getStatus())
                .count();
    }



    //  Conversão Entity -> DTO
    private CategoryResponse entityToDto(Category category) {
        return modelMapper.map(category, CategoryResponse.class);
    }

}
