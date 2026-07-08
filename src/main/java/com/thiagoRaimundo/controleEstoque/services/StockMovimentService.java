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



    public StockMovementResponse createMovement(StockMoevementRequest request){
        Product product = productRepository.findByIdAndStatusTrue(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. ID: " + request.getProductId()));

        Lote lote = loteRepository.findByIdAndStatusTrue(request.getLoteId())
                .orElseThrow(() -> new ResourceNotFoundException("Lote não encontrado. ID: " + request.getLoteId()));

        User user = userRepository.findByIdAndStatusTrue(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado. ID: " + request.getUserId()));

        Stock stock = stockRepository.findByProductId(product.getId())
                .orElseThrow(() -> new StockNotFoundException("Estoque não encontrado para o produto. ID: " + product.getId()));


        //testa se lote pertence ao produto
        if (!lote.getProduct().getId().equals(product.getId())) {
            throw new RuntimeException("O lote não pertence ao produto informado");
        }


        // checa o tipo de movimentação e se o tipo de movimentação foi para retirar então checa se existe a quantidade suficiente
        if (request.getTipo() == TipoStockMoviment.COMPRA || request.getTipo() == TipoStockMoviment.AJUSTE_POSITIVO ||
                request.getTipo() == TipoStockMoviment.DEVOLUCAO) {

            stock.setQuantidadeAtual(stock.getQuantidadeAtual() + request.getQuantidade());

        } else {

            if (stock.getQuantidadeAtual() > request.getQuantidade() || lote.getQuantProdutos() > request.getQuantidade()) {

                lote.setQuantProdutos(lote.getQuantProdutos() - request.getQuantidade());
                stock.setQuantidadeAtual(stock.getQuantidadeAtual() - request.getQuantidade());
            }
            else {
                throw new InsufficientStock("Quantidade de produtos induficiente. Estoque Disponivel:" + stock.getQuantidadeAtual() + "Lote Disponivel: "+ lote.getQuantProdutos());

            }
        }

        StockMovement movement = new StockMovement();
        movement.setTipo(request.getTipo());
        movement.setQuantidade(request.getQuantidade());
        movement.setObservacao(request.getObservacao());
        movement.setDataHora(LocalDateTime.now());
        movement.setUser(user);
        movement.setProduct(product);
        movement.setLote(lote);
        movement.setStatus(true);

        loteRepository.save(lote);
        stockRepository.save(stock);

        StockMovement savedMovement = SMRepository.save(movement);
        return entidadeToDTO(savedMovement);

    }


    public void deleteMovement(Long movementId){
        SMRepository.deleteById(movementId);
    }


    @Transactional
    public StockMovementResponse entradaItens(StockMoevementRequest request) {

        Lote lote = loteRepository.findByIdAndStatusTrue(request.getLoteId()).orElseThrow(()-> new ResourceNotFoundException("O lote informado não existe. ID : "+ request.getLoteId()));
        User u = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("Usuario não foi encontrado. ID: " + request.getUserId()));
        Product product = productRepository.findByIdAndStatusTrue(request.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Produto Informado Não Cadastrado:" + request.getProductId()));
        Stock stock = stockRepository.findByProductId(product.getId()).orElseThrow(() -> new RuntimeException("O stock para o produto não foi encontrado"));


        lote.setQuantProdutos(lote.getQuantProdutos() + request.getQuantidade());
        loteRepository.save(lote);

        stock.setQuantidadeAtual(stock.getQuantidadeAtual() + request.getQuantidade());
        stockRepository.save(stock);


        StockMovement stockMovement = new StockMovement();
        stockMovement.setProduct(product);
        stockMovement.setUser(u);
        stockMovement.setLote(lote);
        stockMovement.setTipo(TipoStockMoviment.AJUSTE_POSITIVO);
        stockMovement.setObservacao("Entrada de produtos - Lote: " + lote.getCodigo());
        stockMovement.setQuantidade(request.getQuantidade());
        stockMovement.setDataHora(LocalDateTime.now());
        stockMovement.setStatus(true);

        StockMovement savedMovement = SMRepository.save(stockMovement);
        return entidadeToDTO(savedMovement);
    }


    @Transactional
    public void consumoItensFEFO(Long productId, int quantidade, Long userId) {

        Product product = productRepository.findByIdAndStatusTrue(productId).orElseThrow(()-> new ResourceNotFoundException("Produto informado não existe. ID:" + productId));
        List<Lote> lotes = loteRepository.findByProductIdOrderByValidateAsc(productId); // alinha os lotes por data de validade dos produtos
        Stock stock = stockRepository.findByProductId(productId).orElseThrow(() -> new StockNotFoundException("Stock não encontrado para o produto" + productId));
        User user = userRepository.findByIdAndStatusTrue(userId).orElseThrow(()-> new ResourceNotFoundException("Usuario informado não existe. ID : "+ userId));


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
            movement.setLote(lote);
            movement.setTipo(TipoStockMoviment.VENDA);
            movement.setQuantidade(retirada);
            movement.setDataHora(LocalDateTime.now());
            movement.setObservacao("Consumo FEFO - Lote: " + lote.getCodigo());
            movement.setStatus(true);
            SMRepository.save(movement);

            restantes -= retirada;

        }

    }


    @Transactional
    public void consumoItens(Long productId, int quantidade, Long idUser, TipoStockMoviment tipoStockMoviment) {

        Product product = productRepository.findByIdAndStatusTrue(productId).orElseThrow(()-> new ResourceNotFoundException("Produto informado não existe. ID:" + productId));
        Stock stock = stockRepository.findByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("Stock não encontrado para o produto" + productId));
        User user = userRepository.findByIdAndStatusTrue(idUser).orElseThrow(() -> new ResourceNotFoundException("User informado não foi encontrado. ID: " + idUser));

        if (stock.getQuantidadeAtual()<quantidade) {
            throw new InsufficientStock("Estoque insuficiente. Estoque atual: "+ stock.getQuantidadeAtual());
        }

        stock.setQuantidadeAtual(stock.getQuantidadeAtual()-quantidade);
        stockRepository.save(stock);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setUser(user);
        movement.setTipo(tipoStockMoviment);
        movement.setQuantidade(quantidade);
        movement.setDataHora(LocalDateTime.now());
        movement.setStatus(true);

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
        Product product = productRepository.findByIdAndStatusTrue(request.getProductId()).orElseThrow(()-> new ResourceNotFoundException("O produto informado não existe. ID: "+request.getProductId()));

        stockMoevement.setQuantidade(request.getQuantidade());
        stockMoevement.setProduct(product);
        stockMoevement.setDataHora(LocalDateTime.now());
        stockMoevement.setObservacao(request.getObservacao());
        stockMoevement.setTipo(request.getTipo());
        stockMoevement.setUser(user);

        SMRepository.save(stockMoevement);
        return entityToDTO(stockMoevement);

    }


    public Page<StockMovementResponse> listarMovimentosPorProduto(Long productId, Pageable pageable) {
        return SMRepository.findByProductIdOrderByDataHoraDesc(productId, pageable).map(this::entidadeToDTO);
    }

    public Page<StockMovementResponse> listarMovimentosPorTipo(TipoStockMoviment tipo, Pageable pageable) {
        return SMRepository.findByTipoOrderByDataHoraDesc(tipo, pageable).map(this::entidadeToDTO);
    }

    public Page<StockMovementResponse> listarMovimentosPorUsuario(Long userId, Pageable pageable) {
        return SMRepository.findByUserIdOrderByDataHoraDesc(userId, pageable)
                .map(this::entidadeToDTO);
    }


    public List<StockMovementResponse> listarMovimentosPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return SMRepository.findByDataHoraBetween(inicio, fim, PageRequest.of(0,10))
                .stream()
                .map(this::entidadeToDTO).collect(Collectors.toList());
    }

    public StockMovementResponse buscarMovimentacaoPorID(Long idMovimentacao){
        StockMovement stockMovement = SMRepository.findById(idMovimentacao).orElseThrow(()-> new ResourceNotFoundException("Movimentação com o ID: "+idMovimentacao+" não foi encontrado."));
        return entidadeToDTO(stockMovement);

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

    private StockMovementResponse entidadeToDTO(StockMovement movement){

        StockMovementResponse dto = new StockMovementResponse();
        dto.setId(movement.getId());
        dto.setTipo(movement.getTipo());
        dto.setQuantidade(movement.getQuantidade());
        dto.setObservacao(movement.getObservacao());
        dto.setDataHora(movement.getDataHora());
        dto.setUserId(movement.getUser().getId());
        dto.setUserName(movement.getUser().getName());
        dto.setLoteId(movement.getLote().getId());
        dto.setLoteCodigo(movement.getLote().getCodigo());
        dto.setProduct(movement.getProduct().getId());
        dto.setProductName(movement.getProduct().getName());

        return dto;
    }





}
