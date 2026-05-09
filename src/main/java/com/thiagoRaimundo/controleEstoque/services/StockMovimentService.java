package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.exceptions.StockNotFoundException;
import com.thiagoRaimundo.controleEstoque.exceptions.UserNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.*;
import com.thiagoRaimundo.controleEstoque.DTOs.StockMovementResponse;
import com.thiagoRaimundo.controleEstoque.models.Enum.TipoStockMoviment;
import com.thiagoRaimundo.controleEstoque.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockMovimentService {

    private StockMovimentRepository SMRepository;
    private StockRepository stockRepository;
    private LoteRepository loteRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;

    public StockMovimentService(StockMovimentRepository SMRepository, StockRepository stockRepository, LoteRepository loteRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.SMRepository = SMRepository;
        this.loteRepository = loteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;

    }

    @Transactional
    public StockMovement entrada(Long idLote, Long idUser, int quantidade) {

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade informada deve ser maior que zero");
        }

        Lote l = loteRepository.findById(idLote)
                .orElseThrow(() -> new ResourceNotFoundException("Lote Não Encontrado"));

        User u = userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("Usuario não foi encontrado"));

        Stock stock = stockRepository.findByProduct_Id(l.getProduct().getId()).orElseThrow(() -> new RuntimeException("O stock do produto não foi encontrado"));


        l.setQuantAtual(l.getQuantAtual() + quantidade);
        loteRepository.save(l);

        stock.setQuantidadeAtual(stock.getQuantidadeAtual() + quantidade);
        stockRepository.save(stock);

        StockMovement stockMovement = new StockMovement();
        stockMovement.setLote(l);
        stockMovement.setProduct(l.getProduct());
        stockMovement.setUser(u);
        stockMovement.setTipo(TipoStockMoviment.COMPRA);
        stockMovement.setQuantidade(quantidade);
        stockMovement.setDataHora(LocalDateTime.now());


        return SMRepository.save(stockMovement);
    }


    @Transactional
    public void consumoFEFO(Product product, int quantidade, User user) {

        List<Lote> lotes = loteRepository.findByProductIdOrderByDataValidadeAsc(product.getId()); // alinha os produtos de um lote pela data de validade
        int restantes = quantidade;

        for (Lote lote : lotes) {
            if (restantes <= 0) break;

            int disponivel = lote.getQuantAtual();

            if (disponivel <= 0) continue;

            int retirada = Math.min(disponivel, restantes); // define quanto retirar do lote

            lote.setQuantAtual(disponivel - retirada);

            Stock stock = stockRepository.findByProduct_Id(product.getId()).orElseThrow(() -> new StockNotFoundException("Stock não encontrado para o produto" + product.getId()));
            stock.setQuantidadeAtual(stock.getQuantidadeAtual() - retirada);

            StockMovement movement = new StockMovement();
            movement.setProduct(product);
            movement.setLote(lote);
            movement.setUser(user);
            movement.setTipo(TipoStockMoviment.VENDA);
            movement.setQuantidade(retirada);
            movement.setDataHora(LocalDateTime.now());

            SMRepository.save(movement);
            loteRepository.save(lote);
            stockRepository.save(stock);

            restantes -= retirada;

        }

        if (restantes > 0) {
            throw new RuntimeException("Estoque insuficiente");
        }
    }

    @Transactional
    public StockMovement usoInterno(Long idLote, Long idUser, int quantidade) {

        Lote l = loteRepository.findById(idLote)
                .orElseThrow(() -> new ResourceNotFoundException("Lote Não Encontrado"));
        User u = userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("Usuario não foi encontrado"));

        l.setQuantAtual(l.getQuantAtual() - quantidade);
        loteRepository.save(l);

        StockMovement stockMovement = new StockMovement();
        stockMovement.setLote(l);
        stockMovement.setProduct(l.getProduct());
        stockMovement.setUser(u);
        stockMovement.setTipo(TipoStockMoviment.USO_INTERNO);
        stockMovement.setQuantidade(quantidade);
        stockMovement.setDataHora(LocalDateTime.now());

        return SMRepository.save(stockMovement);

    }

    @Transactional
    public void ajustarEstoque(Long productId, Long loteId, int quantidadeCorrigida, String observacao, User user) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Stock stock = stockRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new StockNotFoundException("Estoque não encontrado"));

        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RuntimeException("Lote não encontrado"));

        User user1 = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("usuario não encontrado"));

        if (!lote.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Lote não pertence a este produto");
        }

        int diferenca = quantidadeCorrigida - stock.getQuantidadeAtual();
        int novaQuantidadeLote = lote.getQuantAtual() + diferenca;

        if (novaQuantidadeLote < 0) {
            throw new RuntimeException(
                    "Ajuste resultaria em quantidade negativa. Máximo permitido: " + lote.getQuantAtual()
            );
        }

        lote.setQuantAtual(novaQuantidadeLote);
        loteRepository.save(lote);

        stock.setQuantidadeAtual(quantidadeCorrigida);
        stockRepository.save(stock);

        TipoStockMoviment tipo = diferenca > 0 ? TipoStockMoviment.AJUSTE_POSITIVO : TipoStockMoviment.AJUSTE_NEGATIVO;

        StockMovement stockMovement = new StockMovement();
        stockMovement.setLote(lote);
        stockMovement.setProduct(product);
        stockMovement.setUser(user1);
        stockMovement.setTipo(tipo);
        stockMovement.setQuantidade(diferenca);
        stockMovement.setDataHora(LocalDateTime.now());
        stockMovement.setObservacao(observacao);

        SMRepository.save(stockMovement);

    }


    // Listar movimentos por produto
    public Page<StockMovementResponse> listarMovimentosPorProduto(Long productId, Pageable pageable) {
        return SMRepository.findByProductIdOrderByDataHoraDesc(productId, pageable).map(this::converterParaDTO);
    }

    // Listar movimentos por tipo
    public Page<StockMovementResponse> listarMovimentosPorTipo(TipoStockMoviment tipo, Pageable pageable) {
        return SMRepository.findByTipoOrderByDataHoraDesc(tipo, pageable)
                .map(this::converterParaDTO);
    }

    // Listar movimentos por usuário
    public Page<StockMovementResponse> listarMovimentosPorUsuario(Long userId, Pageable pageable) {
        return SMRepository.findByUserIdOrderByDataHoraDesc(userId, pageable)
                .map(this::converterParaDTO);
    }

    // Listar movimentos em intervalo de datas
    public List<StockMovementResponse> listarMovimentosPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return SMRepository.findByDataHoraBetween(inicio, fim)
                .stream()
                .map(this::converterParaDTO).collect(Collectors.toList());
    }

    // Gerar relatório de movimentos por tipo
    public Map<TipoStockMoviment, Integer> gerarRelatorioPorTipo() {
        return SMRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        StockMovement::getTipo,
                        Collectors.summingInt(StockMovement::getQuantidade)
                ));
    }

    // Obter histórico completo de um lote
    public List<StockMovementResponse> obterHistoricoLote(Long batchId) {
        return SMRepository.findByLoteIdOrderByDataHoraDesc(batchId)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }



    private StockMovementResponse converterParaDTO(StockMovement movement) {
        StockMovementResponse stockMovementDTO = new StockMovementResponse();
        stockMovementDTO.setLote(movement.getLote());
        stockMovementDTO.setProduct(movement.getProduct());
        stockMovementDTO.setQuantidade(movement.getQuantidade());
        stockMovementDTO.setUser(movement.getUser());
        stockMovementDTO.setDataHora(movement.getDataHora());
        stockMovementDTO.setTipo(movement.getTipo());
        stockMovementDTO.setObservacao(movement.getObservacao());

        return stockMovementDTO;
    }


}
