package com.thiagoRaimundo.controleEstoque.controllers;

import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemResponse;
import com.thiagoRaimundo.controleEstoque.services.SaleItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/sale-items")
public class SaleItemController {

    private SaleItemService saleItemService;

    public SaleItemController(SaleItemService saleItemService) {
        this.saleItemService = saleItemService;
    }

    @PostMapping
    public ResponseEntity<SaleItemResponse> criarItem(@Valid @RequestBody SaleItemRequest saleItemRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(saleItemService.creatItem(saleItemRequest));
    }

    @GetMapping
    public ResponseEntity<List<SaleItemResponse>> buscarItens(){
        return ResponseEntity.ok(saleItemService.getItens());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItemResponse> buscarItensPorId(@Validated @PathVariable Long id){
        return ResponseEntity.ok(saleItemService.getItemID(id));
    }

    @GetMapping("product/{id}")
    public ResponseEntity<List<SaleItemResponse>> buscarItensPorIdPorduto(@Validated @PathVariable Long id){
        return ResponseEntity.ok(saleItemService.getItensByProductId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleItemResponse> atualizarIten(@Validated @PathVariable Long id,@Valid @RequestBody SaleItemRequest saleItemRequest){
        return ResponseEntity.ok(saleItemService.updateSaleItem(id,saleItemRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarItens(@Validated @PathVariable Long id){
       saleItemService.delete(id);
       return ResponseEntity.noContent().build();

    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SaleItemResponse>> getItensByProduct(@Valid @PathVariable Long productId) {
        return ResponseEntity.ok(saleItemService.getItensByProductId(productId));
    }




}
