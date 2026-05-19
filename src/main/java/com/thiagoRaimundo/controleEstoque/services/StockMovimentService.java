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

    public StockMovimentService(StockMovimentRepository SMRepository, StockRepository stockRepository, LoteRepository loteRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.SMRepository = SMRepository;
        this.loteRepository = loteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;

    }


    @Transactional
    public StockMovementResponse entradaItens(Lote lote, Long idUser) {

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
        stockMovement.setLote(lote);
        stockMovement.setTipo(TipoStockMoviment.COMPRA);
        stockMovement.setQuantidade(lote.getQuantAtual());
        stockMovement.setDataHora(LocalDateTime.now());

        SMRepository.save(stockMovement);

        return entityToDTO(stockMovement);
    }


    @Transactional
    public void consumoItensFEFO(Long productId, int quantidade, User user) {

        Product product = productRepository.findByIdAndStatusTrue(productId).orElseThrow(()-> new ResourceNotFoundException("Produto informado não existe. ID:" + productId));
        List<Lote> lotes = loteRepository.findByProductIdOrderByDataValidadeAsc(productId); // alinha os produtos de um lote pela data de validade
        Stock stock = stockRepository.findByProductId(productId).orElseThrow(() -> new StockNotFoundException("Stock não encontrado para o produto" + productId));

        if (stock.getQuantidadeAtual()<quantidade) {
            throw new InsufficientStock("Estoque insuficiente. Estoque atual: "+stock.getQuantidadeAtual());
        }

        int restantes = quantidade;

        for (Lote lote : lotes) {
            if (restantes <= 0) break;

            int disponivel = lote.getQuantAtual();

            if (disponivel <= 0) continue;

            int retirada = Math.min(disponivel, restantes); // define quanto retirar do lote


            lote.setQuantAtual(disponivel - retirada);
            loteRepository.save(lote);

            stock.setQuantidadeAtual(stock.getQuantidadeAtual() - retirada);
            stockRepository.save(stock);

            StockMovement movement = new StockMovement();
            movement.setProduct(product);
            movement.setLote(lote);
            movement.setUser(user);
            movement.setTipo(TipoStockMoviment.VENDA);
            movement.setQuantidade(retirada);
            movement.setDataHora(LocalDateTime.now());

            SMRepository.save(movement);
            restantes -= retirada;

        }

    }


    @Transactional
    public Boolean consumoItens(Long productId, int quantidade, User user) {

        Product product = productRepository.findByIdAndStatusTrue(productId).orElseThrow(()-> new ResourceNotFoundException("Produto informado não existe. ID:" + productId));
        List<Lote> lotes = loteRepository.findByProductIdOrderByDataValidadeAsc(productId); // alinha os produtos de um lote pela data de validade
        Stock stock = stockRepository.findByProductId(productId).orElseThrow(() -> new StockNotFoundException("Stock não encontrado para o produto" + productId));

        if (stock.getQuantidadeAtual()<quantidade) {
            throw new InsufficientStock("Estoque insuficiente. Estoque atual: "+stock.getQuantidadeAtual());
        }

        stock.setQuantidadeAtual(stock.getQuantidadeAtual()-quantidade);
        stockRepository.save(stock);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setUser(user);
        movement.setTipo(TipoStockMoviment.VENDA);
        movement.setQuantidade(quantidade);
        movement.setDataHora(LocalDateTime.now());

        SMRepository.save(movement);

        return true;

    }

    @Transactional
    public void usoInterno(Long idLote, Long idUser, int quantidade) {

        Lote l = loteRepository.findById(idLote)
                .orElseThrow(() -> new ResourceNotFoundException("Lote Não Encontrado. ID: " + idLote));
        User u = userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException("Usuario não foi encontrado. ID : " +idUser ));

        if(l.getQuantAtual() < quantidade){
            throw new IllegalArgumentException("A quantidade informada excede a quantidade de produtos no lote. Quantidade de prodiutos no lote: "+l.getQuantAtual());
        }

        l.setQuantAtual(l.getQuantAtual() - quantidade);
        loteRepository.save(l);

        StockMovement stockMovement = new StockMovement();
        stockMovement.setLote(l);
        stockMovement.setProduct(l.getProduct());
        stockMovement.setUser(u);
        stockMovement.setTipo(TipoStockMoviment.USO_INTERNO);
        stockMovement.setQuantidade(quantidade);
        stockMovement.setDataHora(LocalDateTime.now());


    }

    @Transactional
    public void ajustarEstoque(Long productId, Long loteId, int quantidadeCorrigida, String observacao, User user) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Stock stock = stockRepository.findByProductId(productId)
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
                    "Ajuste resultaria em quantidade negativa. Máximo permitido de retirada de produtos para esse lote: " + lote.getQuantAtual()
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

    public void devolverProduto(Long idProduto, LocalDate validade, int quantidade){
        Product product = productRepository.findByIdAndStatusTrue(idProduto).orElseThrow(()-> new ResourceNotFoundException("Priduto informado não existe. ID: "+ idProduto));

        Lote lote = loteRepository.findByValidate(validade).orElseThrow(() -> new ResourceNotFoundException("Não há lote que corresponda à data de validade do produto."));
        lote.setQuantAtual(lote.getQuantAtual()+quantidade);
        loteRepository.save(lote);

        Stock stock = stockRepository.findByProductId(idProduto).orElseThrow(()-> new ResourceNotFoundException("Não exite um stock para esse produto !"));
        stock.setQuantidadeAtual(stock.getQuantidadeAtual()+quantidade);
        stockRepository.save(stock);
    }

    public StockMovementResponse updateStockMoviment(Long idStockMoviment, StockMoevementRequest request){

        StockMovement stockMoevement = SMRepository.findById(idStockMoviment).orElseThrow(()-> new ResourceNotFoundException("A movimentação informada não existe. ID: "+idStockMoviment));

        stockMoevement.setLote(request.getLote());
        stockMoevement.setQuantidade(request.getQuantidade());
        stockMoevement.setProduct(request.getProduct());
        stockMoevement.setDataHora(request.getDataHora());
        stockMoevement.setObservacao(request.getObservacao());
        stockMoevement.setTipo(request.getTipo());
        stockMoevement.setUser(request.getUser());

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
        return SMRepository.findByDataHoraBetween(inicio, fim)
                .stream()
                .map(this::entityToDTO).collect(Collectors.toList());
    }

    public StockMovementResponse buscarMovimentacaoPorID(Long idMovimentacao){
        StockMovement stockMovement = stockRepository.findByProductId(idMovimentacao).orElseThrow(()-> new ResourceNotFoundException(""));

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
