package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.StockResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.DTOs.StockRequest;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Stock;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import com.thiagoRaimundo.controleEstoque.repository.StockRepository;
import com.thiagoRaimundo.controleEstoque.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockService {

    private StockRepository stockRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;

    private ModelMapper modelMapper;


    public StockService(StockRepository stockRepositor, ProductRepository productRepository,UserRepository userRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;

    }


    public StockResponse creatStock(StockRequest stockRequest){
        Product product = productRepository.findById(stockRequest.getProduct().getId()).orElseThrow(()-> new ResourceNotFoundException("Produto informado não existe. ID: "+stockRequest.getProduct().getId()));
        Stock stock = DTOToEntity(stockRequest);
        stockRepository.save(stock);
        return entityToDto(stock);
    }


    public StockResponse getStock(Long idStock){
        Stock stock = stockRepository.findById(idStock).orElseThrow(()-> new RuntimeException("O estoque informado não existe. ID: "+idStock));
        return entityToDto(stock);
    }

    public StockResponse getStoctByProductId(Long idProduct){

        if(productRepository.existsByIdAndStatusTrue(idProduct)){
            throw new ResourceNotFoundException("O produto informado não existe. ID :"+ idProduct);
        }

        Stock stock = stockRepository.findByProductId(idProduct).orElseThrow(() -> new ResourceNotFoundException("Não existe um estoque com para esse produto. ID produto: "+ idProduct));

        return entityToDto(stock);

    }

    public List<StockResponse> getStocks(){
        return stockRepository.findAll().stream().map(this::entityToDto).toList();
    }

    public StockResponse updateStock(Long idStock, StockRequest stockRequest){

        Stock stock = stockRepository.findById(idStock).orElseThrow(()-> new ResourceNotFoundException("O Stock informado não existe. ID :"+ idStock));

        if(!productRepository.existsByIdAndStatusTrue(stockRequest.getProduct().getId())){
            throw new ResourceNotFoundException("O Produto informado não existe para atualização. ID :"+ stockRequest.getProduct().getId());
        }

        stock.setProduct(stockRequest.getProduct());
        stock.setQuantidadeAtual(stockRequest.getQuantidadeAtual());
        stock.setQuantidadeMinima(stockRequest.getQuantidadeMinima());
        stock.setQuantidadeMaxima(stockRequest.getQuantidadeMaxima());

        stockRepository.save(stock);
        return entityToDto(stock);

    }

    public void deleteLogfico(Long idStock, String motivo){

        Stock stock = stockRepository.findByIdAndStatusTrue(idStock).orElseThrow(()-> new ResourceNotFoundException("O Stock informado não existe. ID :"+ idStock));
        stock.setStatus(false);
        stock.setDaleteAt(LocalDateTime.now());
        stock.setDeleteReason(motivo);

        stockRepository.save(stock);

        //desenvolver a logica de verificação de usuario valido
    }




    //conversão entre entidades e DTOs

    private Stock DTOToEntity(StockRequest stockRequest){
        return modelMapper.map(stockRequest, Stock.class);
    }

    private StockResponse entityToDto(Stock stock){
        return modelMapper.map(stock, StockResponse.class);
    }



}
