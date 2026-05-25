package com.thiagoRaimundo.controleEstoque.controllers;

import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemResponse;
import com.thiagoRaimundo.controleEstoque.services.SaleItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/SaleItem")
@Valid
public class SaleItemController {

    private SaleItemService saleItemService;

    public SaleItemController(SaleItemService saleItemService) {
        this.saleItemService = saleItemService;
    }

    @PostMapping
    public ResponseEntity<SaleItemResponse> criarItem(@RequestBody SaleItemRequest saleItemRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(saleItemService.creatItem(saleItemRequest));
    }

    @GetMapping
    public ResponseEntity<List<SaleItemResponse>> buscarItens(){
        return ResponseEntity.ok(saleItemService.getItens());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItemResponse> buscarItensPorId(@PathVariable Long id){
        return ResponseEntity.ok(saleItemService.getItemID(id));
    }

    @GetMapping("product/{id}")
    public ResponseEntity<List<SaleItemResponse>> buscarItensPorIdPorduto(@PathVariable Long id){
        return ResponseEntity.ok(saleItemService.getItensByProductId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleItemResponse> atualizarIten(@PathVariable Long id, @RequestBody SaleItemRequest saleItemRequest){
        return ResponseEntity.ok(saleItemService.updateSaleItem(id,saleItemRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarItens(@PathVariable Long id){
       saleItemService.delete(id);
       return ResponseEntity.noContent().build();

    }




}
