package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.SaleItem;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import com.thiagoRaimundo.controleEstoque.repository.SaleItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SaleItemService{

    private SaleItemRepository itemRepository;
    private ProductRepository productRepository;


    public SaleItemService(SaleItemRepository itemRepository, ProductRepository productRepository) {
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
    }

    public SaleItem creatItem(SaleItem item){

        productRepository.findById(item.getProduct().getId()).orElseThrow(()-> new ResourceNotFoundException("Producto correspondente a este item não existe"));
        if(item.getQuantidade()< 0){
            throw new RuntimeException("A quantidade passada não pode ser inferior a zero");
        }
        BigDecimal valorBase = new BigDecimal(0);

        if(item.getValorVenda().compareTo(valorBase) == -1 || item.getValorVenda().compareTo(valorBase) == 0){
            throw new IllegalArgumentException("O valor do Item a ser vendido nao pode ser igual ou menor que zero");
        }

        return itemRepository.save(item);



    }




}

