package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.ProductRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.ProductResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;

import com.thiagoRaimundo.controleEstoque.models.Category;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.repository.CategoryRepository;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductService {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    public ProductResponse getProduct(Long idProduct){
        Product product = productRepository.findByIdAndStatusTrue(idProduct).orElseThrow(()-> new ResourceNotFoundException("Produto não encontrdo. ID: "+ idProduct));
        return entityToDto(product);
    }

    public ProductResponse getByName(String name){
        Product product = productRepository.findByNameAndStatusTrue(name).orElseThrow(()-> new ResourceNotFoundException("Produto não encontrdo, Nome: "+ name));
        return entityToDto(product);
    }

    public List<ProductResponse> getProducts(){
        return productRepository.findByStatusTrue().stream().map(this::entityToDto).toList();
    }

    @Transactional
    public ProductResponse creatProduct(ProductRequest productRequest){
        Category category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(()-> new ResourceNotFoundException("Categoria não encontrada. ID: "+ productRequest.getCategoryId()));

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setCategory(category);
        product.setStatus(true);

        Product savefProduct = productRepository.save(product);

        return entityToDto(product);
    }


    @Transactional
    public void DeleteProduct (Long idProduct){
        Product product = productRepository.findByIdAndStatusTrue(idProduct).orElseThrow(() -> new ResourceNotFoundException("O Produto informado não existe. ID: "+ idProduct));
        product.setStatus(false);
        productRepository.save(product);

    }

    @Transactional
    public ProductResponse updateProduct(Long idProduct, ProductRequest productRequest){

        Product product = productRepository.findByIdAndStatusTrue(idProduct).orElseThrow(() -> new ResourceNotFoundException("O produto informado não existe. ID: "+idProduct));

        if(productRequest.getName() != null){product.setName(productRequest.getName());}
        if(productRequest.getDescription() != null){product.setDescription(productRequest.getDescription());}
        if(productRequest.getCategoryId() != null){
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(()->new ResourceNotFoundException("Categoria não encontrada. ID: "+productRequest.getCategoryId()));
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        return entityToDto(updatedProduct);

    }

    public List<ProductResponse> listarPorCategoria(Long idCategory){

        if(!categoryRepository.existsById(idCategory)){
            throw new ResourceNotFoundException("A Categoria informada não existe. ID: "+ idCategory);
        }

        return productRepository.findByCategoryIdAndStatusTrue(idCategory).stream().map(this::entityToDto).toList();

    }


    private ProductResponse entityToDto(Product product){
        return modelMapper.map(product, ProductResponse.class);
    }

    private Product DTOToEntity(ProductRequest productRequest){
        return modelMapper.map(productRequest,Product.class);
    }



}
