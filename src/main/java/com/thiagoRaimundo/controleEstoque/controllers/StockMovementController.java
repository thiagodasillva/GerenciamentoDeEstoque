package com.thiagoRaimundo.controleEstoque.controllers;

import com.thiagoRaimundo.controleEstoque.DTOs.RelatorioTipoMovimentoDTO;
import com.thiagoRaimundo.controleEstoque.services.StockMovimentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/stock-moviment")
public class StockMovementController {


    private StockMovimentService stockMovimentService;

    public StockMovementController(StockMovimentService stockMovimentService) {
        this.stockMovimentService = stockMovimentService;
    }




    @GetMapping("/relatorios/por-tipo")
    public ResponseEntity<List<RelatorioTipoMovimentoDTO>> gerarRelatorioPorTipo() {
        return ResponseEntity.ok(stockMovimentService.gerarRelatorioPorTipo());
    }



}
