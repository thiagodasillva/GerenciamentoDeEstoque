package com.thiagoRaimundo.controleEstoque.controllers;

import com.thiagoRaimundo.controleEstoque.DTOs.RelatorioTipoMovimentoDTO;
import com.thiagoRaimundo.controleEstoque.DTOs.StockMoevementRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.StockMovementResponse;
import com.thiagoRaimundo.controleEstoque.models.Enum.TipoStockMoviment;
import com.thiagoRaimundo.controleEstoque.services.StockMovimentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stock_movements")
public class StockMovementController {

    private StockMovimentService stockMovimentService;

    public StockMovementController(StockMovimentService stockMovimentService) {
        this.stockMovimentService = stockMovimentService;
    }

    @PostMapping
    public ResponseEntity<StockMovementResponse> createMovement(@Valid @RequestBody StockMoevementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockMovimentService.createMovement(request));
    }

    @PostMapping("/entrada")
    public ResponseEntity<StockMovementResponse> entradaItens(@Valid @RequestBody StockMoevementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockMovimentService.entradaItens(request));
    }

    @PostMapping("/consumo")
    public ResponseEntity<Void> consumoItens( @RequestParam Long productId,@RequestParam int quantidade,@RequestParam Long userId,TipoStockMoviment tipo) {
        stockMovimentService.consumoItens(productId, quantidade, userId, tipo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/consumo-fefo")
    public ResponseEntity<Void> consumoItensFEFO(
            @RequestParam Long productId,
            @RequestParam int quantidade,
            @RequestParam Long userId) {
        stockMovimentService.consumoItensFEFO(productId, quantidade, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ajuste")
    public ResponseEntity<Void> ajustarEstoque(
            @RequestParam Long productId,
            @RequestParam int quantidadeCorrigida,
            @RequestParam String observacao,
            @RequestParam Long userId) {
        stockMovimentService.ajustarEstoque(productId, quantidadeCorrigida, observacao, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/devolucao")
    public ResponseEntity<Void> devolverProduto(
            @RequestParam Long productId,
            @RequestParam int quantidade,
            @RequestParam Long userId) {
        stockMovimentService.devolverProduto(productId, quantidade, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMovementResponse> buscarMovimentacaoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(stockMovimentService.buscarMovimentacaoPorID(id));
    }

    @GetMapping("/produto/{productId}")
    public ResponseEntity<Page<StockMovementResponse>> listarPorProduto(@PathVariable Long productId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(stockMovimentService.listarMovimentosPorProduto(productId, pageable));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Page<StockMovementResponse>> listarPorTipo(@PathVariable TipoStockMoviment tipo, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(stockMovimentService.listarMovimentosPorTipo(tipo, pageable));
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<Page<StockMovementResponse>> listarPorUsuario(@PathVariable Long userId, @PageableDefault(size = 10,page = 0) Pageable pageable) {
        return ResponseEntity.ok(stockMovimentService.listarMovimentosPorUsuario(userId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable Long id) {stockMovimentService.deleteMovement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorios/por-tipo")
    public ResponseEntity<List<RelatorioTipoMovimentoDTO>> gerarRelatorioPorTipo() {
        return ResponseEntity.ok(stockMovimentService.gerarRelatorioPorTipo());
    }



}
