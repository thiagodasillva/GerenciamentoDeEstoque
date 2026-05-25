package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.ProductRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.ProductResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;

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

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse getProduct(Long idProduct){
       Product product = productRepository.findByIdAndStatusTrue(idProduct).orElseThrow(()-> new ResourceNotFoundException("Produto não encontrdo"));
       return entityToDto(product);
    }

    public ProductResponse getByName(String name){
        Product product = productRepository.findByNameAndStatusTrue(name).orElseThrow(()-> new ResourceNotFoundException("Produto não encontrdo"));
        return entityToDto(product);
    }

    public List<ProductResponse> getProducts(){
        return productRepository.findByStatusTrue().stream().map(this::entityToDto).toList();
    }

    public ProductResponse creatProduct(ProductRequest productRequest){
        Product product = productRepository.save(DTOToEntity(productRequest));
        return entityToDto(product);
    }


    @Transactional
    public void DeleteProduct (Long idProduct){
        Product product = productRepository.findByIdAndStatusTrue(idProduct).orElseThrow(() -> new ResourceNotFoundException("O Produto informado não existe"));
        product.setStatus(false);
        productRepository.save(product);

    }

    public ProductResponse updateProduct(Long idProduct, ProductRequest productRequest){
        Product product = productRepository.findByIdAndStatusTrue(idProduct).orElseThrow(() -> new ResourceNotFoundException("O produto informado não existe. ID: "+idProduct));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setCategory(productRequest.getCategory());
        if(productRequest.getLotes() != null){
            product.setLotes(productRequest.getLotes());
        }

        productRepository.save(product);

        return entityToDto(product);

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
