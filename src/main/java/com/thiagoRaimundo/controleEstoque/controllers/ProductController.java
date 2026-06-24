package com.thiagoRaimundo.controleEstoque.controllers;

import com.thiagoRaimundo.controleEstoque.DTOs.ProductRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.ProductResponse;
import com.thiagoRaimundo.controleEstoque.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping
    public ResponseEntity<ProductResponse> criarProduto(@Valid @RequestBody ProductRequest productRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.creatProduct(productRequest));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> buscaProdutoPorId(@Validated @PathVariable Long id){
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping("name/{name}")
    public ResponseEntity<ProductResponse> buscaProdutoPorNome(@Validated @PathVariable String name){
        return ResponseEntity.ok(productService.getByName(name));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> buscaListaProdutos(){
        return ResponseEntity.ok(productService.getProducts());
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> atualizarProduto(@Validated @PathVariable Long id,@Valid @RequestBody ProductRequest productRequest){
        return ResponseEntity.ok(productService.updateProduct(id,productRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@Validated @PathVariable Long id) {
        productService.DeleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
