package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;

import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(Long id){
       return productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Produto não encontrdo"));
    }

    public Product getByName(String name){
        return productRepository.findByName(name).orElseThrow(()-> new ResourceNotFoundException("Produto não encontrdo"));
    }

    public List<Product> getProducts(){
        return productRepository.findAll();
    }

    public Product CreatProduct(Product p){
        return productRepository.save(p);
    }


    @Transactional
    public void DeleteProduct (Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("O Produto informado não existe"));
        product.setStatus(false);
        productRepository.save(product);

    }

    public Product updateProduct(Product product,Long idProduct){

        Optional<Product> pOps = productRepository.findById(idProduct);
        if (pOps.isPresent()){

            Product p = pOps.get();
            productRepository.save(productToProduct(p,product));

            return p;

        }
        throw new ResourceNotFoundException("O produto indormado para atualização não existe");

    }

    public List<Product> listarPorCategoria(Long id){
        List<Product> proCat = productRepository.findByCategory(id);
        return proCat;

    }

    private Product productToProduct(Product a, Product b){
        a.setName(b.getName());
        a.setCategory(b.getCategory());
        a.setDescription(b.getDescription());
        return a;


    }
}
