//package com.thiagoRaimundo.controleEstoque.controllers;
//
//import com.thiagoRaimundo.controleEstoque.services.ProductService;
//import com.thiagoRaimundo.controleEstoque.models.Product;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping(path = "/api/products")
//public class ProductController {
//
//    private ProductService productService;
//
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }
//
//    @GetMapping("{/id}")
//    public ResponseEntity<Product> getById(@PathVariable Long id){
//        return ResponseEntity.ok(productService.getProduct(id));
//    }
//
//    @GetMapping("")
//    public ResponseEntity<Product> getByName(@PathVariable String name){
//        return ResponseEntity.ok(productService.getByName(name));
//    }
//
//    @GetMapping()
//    public ResponseEntity<List<Product>> listProducts(){
//        return ResponseEntity.ok(productService.getProducts());
//    }
//
//
//    @PostMapping()
//    public ResponseEntity<Product> creat(@RequestBody Product product){
//        return ResponseEntity.status(HttpStatus.CREATED).body(productService.CreatProduct(product));
//    }
//
//    @ResponseStatus(HttpStatus.OK)
//    @DeleteMapping("/{id}")
//    public void deleteProduct(@PathVariable Long id){
//    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable Long id){
//        return productService.updateProduct(id,product);
//
//    }

}
