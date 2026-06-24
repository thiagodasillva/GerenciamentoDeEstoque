package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.RelatorioTipoMovimentoDTO;
import com.thiagoRaimundo.controleEstoque.DTOs.StockMoevementRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.StockRequest;
import com.thiagoRaimundo.controleEstoque.exceptions.InsufficientStock;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.exceptions.StockNotFoundException;
import com.thiagoRaimundo.controleEstoque.exceptions.UserNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.*;
import com.thiagoRaimundo.controleEstoque.DTOs.StockMovementResponse;
import com.thiagoRaimundo.controleEstoque.models.Enum.TipoStockMoviment;
import com.thiagoRaimundo.controleEstoque.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
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
    private ModelMapper modelMapper;

    public StockMovimentService(StockMovimentRepository SMRepository, StockRepository stockRepository, LoteRepository loteRepository, UserRepository userRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.SMRepository = SMRepository;
        this.stockRepository = stockRepository;
        this.loteRepository = loteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public StockMovementResponse entradaItens(Lote lote, Long idUser,int quantidade) {

        User u = userRepository.findById(idUser).orElseThrow(() -> new UserNotFoundException("Usuario não foi encontrado. ID: " + idUser));
        Product product = productRepository.findByIdAndStatusTrue(lote.getProduct().getId()).orElseThrow(() -> new ResourceNotFoundException("Produto Informado Não Cadastrado:" + lote.getProduct().getId()));
        Stock stock = stockRepository.findByProductId(lote.getProduct().getId()).orElseThrow(() -> new RuntimeException("O stock dpara o produto não foi encontrado"));

        if(!loteRepository.existsById(lote.getId())){
            throw new ResourceNotFoundException("O lote Informado nao Existe. ID: "+lote.getId());
        }

        stockRepository.save(stock);

        StockMovement stockMovement = new StockMovement();
        stockMovement.setProduct(product);
        stockMovement.setUser(u);
        stockMovement.setTipo(TipoStockMoviment.COMPRA);
        stockMovement.setQuantidade(quantidade);
        stockMovement.setDataHora(LocalDateTime.now());

        SMRepository.save(stockMovement);

        return entityToDTO(stockMovement);
    }


    @Transactional
    public void consumoItensFEFO(Long productId, int quantidade, User user) {

        Product product = productRepository.findByIdAndStatusTrue(productId).orElseThrow(()-> new ResourceNotFoundException("Produto informado não existe. ID:" + productId));
        List<Lote> lotes = loteRepository.findByProductIdOrderByValidateAsc(productId); // alinha os produtos de um lote pela data de validade
        Stock stock = stockRepository.findByProductId(productId).orElseThrow(() -> new StockNotFoundException("Stock não encontrado para o produto" + productId));

        if (stock.getQuantidadeAtual()<quantidade) {
            throw new InsufficientStock("Estoque insuficiente. Estoque atual: "+stock.getQuantidadeAtual());
        }

        int restantes = quantidade;

        for (Lote lote : lotes) {
            if (restantes <= 0) break;

            int disponivel = lote.getQuantProdutos();

            if (disponivel <= 0) continue;

            int retirada = Math.min(disponivel, restantes); // define quanto retirar do lote


            lote.setQuantProdutos(disponivel - retirada);
            loteRepository.save(lote);

            stock.setQuantidadeAtual(stock.getQuantidadeAtual() - retirada);
            stockRepository.save(stock);

            StockMovement movement = new StockMovement();
            movement.setProduct(product);
            movement.setUser(user);
            movement.setTipo(TipoStockMoviment.VENDA);
            movement.setQuantidade(retirada);
            movement.setDataHora(LocalDateTime.now());

            SMRepository.save(movement);
            restantes -= retirada;

        }

    }


    @Transactional
    public void consumoItens(Long productId, int quantidade, Long idUser, TipoStockMoviment tipoStockMoviment) {

        Product product = productRepository.findByIdAndStatusTrue(productId).orElseThrow(()-> new UserNotFoundException("Produto informado não existe. ID:" + productId));
        Stock stock = stockRepository.findByProductId(productId).orElseThrow(() -> new StockNotFoundException("Stock não encontrado para o produto" + productId));
        User user = userRepository.findByIdAndStatusTrue(idUser).orElseThrow(() -> new ResourceNotFoundException("User informado não foi encontrado. ID: " + idUser));

        if (stock.getQuantidadeAtual()<quantidade) {
            throw new InsufficientStock("Estoque insuficiente. Estoque atual: "+stock.getQuantidadeAtual());
        }

        stock.setQuantidadeAtual(stock.getQuantidadeAtual()-quantidade);
        stockRepository.save(stock);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setUser(user);
        movement.setTipo(tipoStockMoviment);
        movement.setQuantidade(quantidade);
        movement.setDataHora(LocalDateTime.now());

        SMRepository.save(movement);

    }

    @Transactional
    public void ajustarEstoque(Long productId, int quantidadeCorrigida, String observacao, Long idUser ) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new StockNotFoundException("Estoque não encontrado"));

        User user = userRepository.findByIdAndStatusTrue(idUser).orElseThrow(() -> new ResourceNotFoundException("User informado não foi encontrado. ID: " + idUser));


        int diferenca = quantidadeCorrigida - stock.getQuantidadeAtual();
        int novaQuantidadeLote = stock.getQuantidadeAtual() + diferenca;

        if (novaQuantidadeLote < 0) {
            throw new RuntimeException(
                    "Ajuste resultaria em quantidade negativa. Máximo permitido de retirada de produtos para esse lote: " + stock.getQuantidadeAtual());
        }


        stock.setQuantidadeAtual(quantidadeCorrigida);
        stockRepository.save(stock);

        TipoStockMoviment tipo = diferenca > 0 ? TipoStockMoviment.AJUSTE_POSITIVO : TipoStockMoviment.AJUSTE_NEGATIVO;

        StockMovement stockMovement = new StockMovement();
        stockMovement.setProduct(product);
        stockMovement.setUser(user);
        stockMovement.setTipo(tipo);
        stockMovement.setQuantidade(diferenca);
        stockMovement.setDataHora(LocalDateTime.now());
        stockMovement.setObservacao(observacao);

        SMRepository.save(stockMovement);

    }

    public void devolverProduto(Long idProduto, int quantidade, Long idUser){

        User user = userRepository.findByIdAndStatusTrue(idUser).orElseThrow(() -> new ResourceNotFoundException("User informado não foi encontrado. ID: " + idUser));
        Product product = productRepository.findByIdAndStatusTrue(idProduto).orElseThrow(()-> new ResourceNotFoundException("Priduto informado não existe. ID: "+ idProduto));
        Stock stock = stockRepository.findByProductId(idProduto).orElseThrow(()-> new ResourceNotFoundException("Não exite um stock para esse produto !"));

        stock.setQuantidadeAtual(stock.getQuantidadeAtual()+quantidade);
        stockRepository.save(stock);

        StockMovement stockMovement = new StockMovement();
        stockMovement.setTipo(TipoStockMoviment.DEVOLUCAO);
        stockMovement.setUser(user);
        stockMovement.setQuantidade(quantidade);
        stockMovement.setProduct(product);
        stockMovement.setDataHora(LocalDateTime.now());
        SMRepository.save(stockMovement);


    }

    public StockMovementResponse updateStockMoviment(Long idStockMoviment, StockMoevementRequest request, Long idUser){

        StockMovement stockMoevement = SMRepository.findById(idStockMoviment).orElseThrow(()-> new ResourceNotFoundException("A movimentação informada não existe. ID: "+idStockMoviment));
        User user = userRepository.findByIdAndStatusTrue(idUser).orElseThrow(() -> new ResourceNotFoundException("User informado não foi encontrado. ID: " + idUser));

        stockMoevement.setQuantidade(request.getQuantidade());
        stockMoevement.setProduct(request.getProduct());
        stockMoevement.setDataHora(LocalDateTime.now());
        stockMoevement.setObservacao(request.getObservacao());
        stockMoevement.setTipo(request.getTipo());
        stockMoevement.setUser(user);

        SMRepository.save(stockMoevement);
        return entityToDTO(stockMoevement);

    }


    public Page<StockMovementResponse> listarMovimentosPorProduto(Long productId, Pageable pageable) {
        return SMRepository.findByProductIdOrderByDataHoraDesc(productId, pageable).map(this::entityToDTO);
    }

    public Page<StockMovementResponse> listarMovimentosPorTipo(TipoStockMoviment tipo, Pageable pageable) {
        return SMRepository.findByTipoOrderByDataHoraDesc(tipo, pageable).map(this::entityToDTO);
    }

    public Page<StockMovementResponse> listarMovimentosPorUsuario(Long userId, Pageable pageable) {
        return SMRepository.findByUserIdOrderByDataHoraDesc(userId, pageable)
                .map(this::entityToDTO);
    }


    public List<StockMovementResponse> listarMovimentosPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return SMRepository.findByDataHoraBetween(inicio, fim, PageRequest.of(0,10))
                .stream()
                .map(this::entityToDTO).collect(Collectors.toList());
    }

    public StockMovementResponse buscarMovimentacaoPorID(Long idMovimentacao){
        StockMovement stockMovement = SMRepository.findById(idMovimentacao).orElseThrow(()-> new ResourceNotFoundException("Movimentação com o ID: "+idMovimentacao+" não foi encontrado."));
        return entityToDTO(stockMovement);

    }

    public List<RelatorioTipoMovimentoDTO> gerarRelatorioPorTipo() {

        Map<TipoStockMoviment, Integer> agrupado =
                SMRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        StockMovement::getTipo,
                        Collectors.summingInt(StockMovement::getQuantidade)
                ));

        Integer totalGeral = agrupado.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        List<RelatorioTipoMovimentoDTO> relatorios =
                agrupado.entrySet().stream()
                .map(entry -> RelatorioTipoMovimentoDTO.builder()
                        .tipo(entry.getKey().name())
                        .quantidadeTotal(entry.getValue())
                        .percentual(totalGeral > 0 ? (entry.getValue() * 100.0) / totalGeral : 0.0)
                        .build())
                .sorted(Comparator.comparing(RelatorioTipoMovimentoDTO::getQuantidadeTotal).reversed())
                .collect(Collectors.toList());

        return relatorios;
    }


    private StockMovement DTOToEntity(StockRequest stockRequest){
        return modelMapper.map(stockRequest, StockMovement.class);
    }

    private StockMovementResponse entityToDTO(StockMovement stockMovement){
        return modelMapper.map(stockMovement, StockMovementResponse.class);
    }





}
