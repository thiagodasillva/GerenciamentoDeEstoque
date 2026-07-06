package com.thiagoRaimundo.controleEstoque.controllers;

import com.thiagoRaimundo.controleEstoque.DTOs.SaleRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleResponse;
import com.thiagoRaimundo.controleEstoque.services.SaleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/sales")
public class SaleController {

    private SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> buscarSalePorId(@Validated  @PathVariable Long id){
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> buscarSales(){
        return ResponseEntity.ok(saleService.getSales());
    }

    @GetMapping("periodo")
    public ResponseEntity<List<SaleResponse>> buscarSalePorPeriodo(@Validated @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,@Validated @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim){
        return ResponseEntity.ok(saleService.getSalesPerPeriods(dataInicio,dataFim));
    }

    @GetMapping("valores-margem")
    public ResponseEntity<List<SaleResponse>> buscarSalesPorMargemDeLucros(@Validated @RequestParam BigDecimal min,@Validated @RequestParam BigDecimal max){
        return ResponseEntity.ok(saleService.getValueMargin(min,max));
    }

    @PostMapping
    public ResponseEntity<SaleResponse> criarVenda(@RequestBody SaleRequest request) {
        return ResponseEntity.ok(saleService.realizarVenda(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleResponse> atualizarVenda(@PathVariable Long id, @RequestBody SaleRequest request) {
        return ResponseEntity.ok(saleService.updateSale(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        saleService.deleteLogico(id);
        return ResponseEntity.noContent().build();
    }










}
