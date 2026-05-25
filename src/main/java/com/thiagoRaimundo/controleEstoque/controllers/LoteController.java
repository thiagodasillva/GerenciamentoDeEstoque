package com.thiagoRaimundo.controleEstoque.controllers;

import com.thiagoRaimundo.controleEstoque.DTOs.LoteRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.LoteResponse;
import com.thiagoRaimundo.controleEstoque.services.LoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/lotes")
@Valid
public class LoteController {

    private LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }



    @PostMapping
    public ResponseEntity<LoteResponse> criarLote(@Valid @RequestBody LoteRequest loteRequest) {
        LoteResponse novoLote = loteService.creatLote(loteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoLote);
    }

    @GetMapping
    public ResponseEntity<List<LoteResponse>> listarTodosLotes() {
        List<LoteResponse> lotes = loteService.getLotes();
        return ResponseEntity.ok(lotes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteResponse> buscarLotePorId(@PathVariable Long id) {
        LoteResponse lote = loteService.getLoteById(id);
        return ResponseEntity.ok(lote);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<LoteResponse> buscarLotePorCodigo(@PathVariable String codigo) {
        LoteResponse lote = loteService.getLoteByCodigo(codigo);
        return ResponseEntity.ok(lote);
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<LoteResponse>> listarLotesPorProdutoOrdenadosPorValidade(@PathVariable Long produtoId) {
        List<LoteResponse> lotes = loteService.getLotesByProdutosOrderByValidadeDate(produtoId);
        return ResponseEntity.ok(lotes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLoteLogicamente(@PathVariable Long id) {
        loteService.deleteLogicoDeLote(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoteResponse> atualizarLote(
            @PathVariable Long id,
            @Valid @RequestBody LoteRequest loteRequest) {
        LoteResponse loteAtualizado = loteService.updateLote(id, loteRequest);
        return ResponseEntity.ok(loteAtualizado);
    }

}
