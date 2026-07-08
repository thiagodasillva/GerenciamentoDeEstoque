package com.thiagoRaimundo.controleEstoque.controllers;

import com.thiagoRaimundo.controleEstoque.DTOs.StockRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.StockResponse;
import com.thiagoRaimundo.controleEstoque.services.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stocks")
public class StockController {


    private StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping()
    public ResponseEntity<StockResponse> create(@Valid @RequestBody StockRequest stockRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.creatStock(stockRequest));
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> getAll(){
        return ResponseEntity.ok(stockService.getStocks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> getId(@PathVariable Long id){
        return ResponseEntity.ok(stockService.getStock(id));
    }

    @GetMapping("/product/{idProduct}")
    public ResponseEntity<StockResponse> getByProductId (@PathVariable Long idProduto){
        return ResponseEntity.ok(stockService.getStoctByProductId(idProduto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockResponse> update(@PathVariable Long id, @Valid @RequestBody StockRequest stockRequest){
        return ResponseEntity.ok(stockService.updateStock(id,stockRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        stockService.deleteLogico(id);
        return ResponseEntity.noContent().build();
    }



}
