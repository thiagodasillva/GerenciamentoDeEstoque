package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.StockResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.DTOs.StockRequest;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Stock;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import com.thiagoRaimundo.controleEstoque.repository.StockRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
@Service
public class StockService {

    private StockRepository stockRepository;
    private ProductRepository productRepository;

    private ModelMapper modelMapper;


    public StockService(StockRepository stockRepositor, ProductRepository productRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;

    }


    public Stock creatStock(StockRequest stock){
        Product product = productRepository.findById(stock.getProduct()).orElseThrow(()-> new ResourceNotFoundException("Produto informado não existe"));
        return stockRepository.save(DtoToEntity(stock));
    }


    public StockResponse getStock(Long id){
        Stock stock = stockRepository.findById(id).orElseThrow(()-> new RuntimeException("O estoque informado não existe"));
        return EntityToDto(stock);
    }

    public StockResponse getStoctByProductId(Long id){
        Product product = productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Produto informado não existe"));
        Stock stock = stockRepository.findByProduct_Id(id).orElseThrow(() -> new ResourceNotFoundException("Não existe um estoque com para esse produto"));
        return EntityToDto(stock);

    }




    //conversão entre entidades e DTOs

    private Stock DtoToEntity(StockRequest stockRequest){
        return modelMapper.map(stockRequest, Stock.class);
    }

    private StockResponse EntityToDto(Stock stock){
        return modelMapper.map(stock, StockResponse.class);
    }



}
