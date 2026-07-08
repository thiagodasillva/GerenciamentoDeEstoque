package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.StockResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.DTOs.StockRequest;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Stock;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import com.thiagoRaimundo.controleEstoque.repository.StockRepository;
import com.thiagoRaimundo.controleEstoque.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockService {


    private StockRepository stockRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public StockService(StockRepository stockRepository, ProductRepository productRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public StockResponse creatStock(StockRequest stockRequest){

        Product product = productRepository.findByIdAndStatusTrue(stockRequest.getProductId())
                .orElseThrow(()-> new ResourceNotFoundException("Produto não encontrado. ID: "+stockRequest.getProductId()));

        if(stockRepository.findByProductId(stockRequest.getProductId()).isPresent()){
            throw new RuntimeException("Ja existem um estoque cadastrado para este produto. ID: "+ stockRequest.getProductId());
        }

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantidadeAtual(stockRequest.getQuantidadeAtual());
        stock.setQuantidadeMaxima(stockRequest.getQuantidadeMaxima());
        stock.setQuantidadeMinima(stockRequest.getQuantidadeMinima());
        stock.setStatus(true);


        Stock savedStock = stockRepository.save(stock);
        return entidadeToDTO(savedStock);
    }


    public StockResponse getStock(Long idStock){
        Stock stock = stockRepository.findById(idStock)
                .orElseThrow(()-> new RuntimeException("O estoque informado não existe. ID: "+idStock));
        return entidadeToDTO(stock);
    }

    public StockResponse getStoctByProductId(Long idProduct){

        if(productRepository.existsByIdAndStatusTrue(idProduct)){
            throw new ResourceNotFoundException("O produto informado não existe. ID :"+ idProduct);
        }

        Stock stock = stockRepository.findByProductId(idProduct)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe um estoque com para esse produto. ID produto: "+ idProduct));

        return entidadeToDTO(stock);

    }

    public List<StockResponse> getStocks(){
        return stockRepository.findAll()
                .stream()
                .map(this::entidadeToDTO)
                .toList();
    }

    @Transactional
    public StockResponse updateStock(Long idStock, StockRequest stockRequest){

        Stock stock = stockRepository.findById(idStock)
                .orElseThrow(()-> new ResourceNotFoundException("O Stock informado não existe. ID :"+ idStock));

        Product product = productRepository.findByIdAndStatusTrue(stockRequest.getProductId())
                .orElseThrow(()-> new ResourceNotFoundException("Produto não encontrado. ID : "+ stockRequest.getProductId()));

        stock.setProduct(product);
        stock.setQuantidadeAtual(stockRequest.getQuantidadeAtual());
        stock.setQuantidadeMinima(stockRequest.getQuantidadeMinima());
        stock.setQuantidadeMaxima(stockRequest.getQuantidadeMaxima());

        Stock updatedStock = stockRepository.save(stock);
        return entidadeToDTO(updatedStock);

    }

    @Transactional
    public void deleteLogico(Long idStock){

        Stock stock = stockRepository.findByIdAndStatusTrue(idStock)
                .orElseThrow(()-> new ResourceNotFoundException("O Stock informado não existe. ID :"+ idStock));
        stock.setStatus(false);
        stock.setDeletedAt(LocalDateTime.now());
        stockRepository.save(stock);
    }


    @Transactional
    public StockResponse updateQuantidade(Long productId, Integer quantidade, boolean isEntrada) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para o produto. ID: " + productId));


        if (isEntrada) {
            stock.setQuantidadeAtual(stock.getQuantidadeAtual() + quantidade);
        } else {
            if (stock.getQuantidadeAtual() < quantidade) {
                throw new RuntimeException("Estoque insuficiente. Disponível: " + stock.getQuantidadeAtual());
            }
            stock.setQuantidadeAtual(stock.getQuantidadeAtual() - quantidade);
        }

        Stock savedStock = stockRepository.save(stock);

        return entidadeToDTO(savedStock);
    }

    private Stock DTOToEntity(StockRequest stockRequest){
        return modelMapper.map(stockRequest, Stock.class);
    }

    //caiu em desuso
    private StockResponse entityToDto(Stock stock){
        return modelMapper.map(stock, StockResponse.class);
    }


    private StockResponse entidadeToDTO(Stock stock){
        StockResponse response = new StockResponse();
        response.setId(stock.getId());
        response.setProduct(stock.getProduct().getId());
        response.setProductName(stock.getProduct().getName());
        response.setQuantidadeAtual(stock.getQuantidadeAtual());
        response.setQuantidadeMinima(stock.getQuantidadeMinima());
        response.setQuantidadeMaxima(stock.getQuantidadeMaxima());
        response.setStatus(stock.getStatus());

        return response;
    }


}
