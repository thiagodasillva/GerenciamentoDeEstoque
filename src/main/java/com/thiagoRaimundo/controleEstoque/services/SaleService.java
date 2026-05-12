package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemResponse;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Sale;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleRequest;
import com.thiagoRaimundo.controleEstoque.models.SaleItem;
import com.thiagoRaimundo.controleEstoque.models.User;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import com.thiagoRaimundo.controleEstoque.repository.SaleItemRepository;
import com.thiagoRaimundo.controleEstoque.repository.SaleRepository;
import com.thiagoRaimundo.controleEstoque.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private SaleRepository saleRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private StockMovimentService stockMovimentService;

    private SaleItemRepository saleItemRepository;
    private ModelMapper modelMapper;

    public SaleService(SaleRepository saleRepository, UserRepository userRepository, ProductRepository productRepository, StockMovimentService stockMovimentService, SaleItemRepository saleItemRepository) {
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockMovimentService = stockMovimentService;
        this.saleItemRepository = saleItemRepository;
    }

    public SaleResponse creatSale(SaleRequest saleRequest){
        Sale sale = DTOToEntity(saleRequest);
        saleRepository.save(sale);
        return entityToDTO(sale);
    }

    public SaleResponse getSaleById(Long idSale){
        Sale sale = saleRepository.findByIdAndStatusTrue(idSale).orElseThrow(()-> new ResourceNotFoundException("Venda não Encontrada. ID: "+idSale));
        return entityToDTO(sale);
    }

    public List<SaleResponse> getSales(){
        return saleRepository.findByStatusTrue().stream().map(this::entityToDTO).toList();
    }

    // pesquisar por data

    //pesquisar por valor

    public SaleResponse updateSale(Long idSale, SaleRequest saleRequest){

        Sale sale = saleRepository.findByIdAndStatusTrue(idSale).orElseThrow(()-> new ResourceNotFoundException("Venda não Encontrada. ID: "+idSale));

        if(!userRepository.existsById(saleRequest.getUser().getId())){
            throw new ResourceNotFoundException("o usuario Informado não existe. ID: "+ saleRequest.getUser().getId());
        }
        sale.setDataVenda(saleRequest.getDataVenda());
        sale.setUser(saleRequest.getUser());
        sale.setValorTotal(saleRequest.getValorTotal());
        saleRepository.save(sale);

        return entityToDTO(sale);

    }

    // incluir metodo de Acrescentar itens a lista, remover itens da lista

    public void delete(Long idSale){
        Sale sale = saleRepository.findByIdAndStatusTrue(idSale).orElseThrow(()-> new ResourceNotFoundException("Venda não Encontrada. ID: "+idSale));
        //incluir função para guardar dados do usuario que apagou
        sale.setStatus(false);

    }


    // analisar esse metodo depois
    @Transactional
    public Sale realizarVenda(SaleRequest dto) {

        User user = userRepository.findById(dto.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Sale sale = new Sale();
        sale.setUser(user);
        sale.setDataVenda(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (SaleItemRequest itemDTO : dto.getItens()) {

            Product product = productRepository.findById(itemDTO.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            stockMovimentService.consumoFEFO(product, itemDTO.getQuantidade(), user);

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProduct(product);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setValorVenda(itemDTO.getValorVenda());

            BigDecimal subtotal = itemDTO.getValorVenda()
                    .multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));

            item.setSubTotal(subtotal);
            sale.getItens().add(item);

            total = total.add(subtotal);
        }

        sale.setValorTotal(total);

        return saleRepository.save(sale);
    }



    private Sale DTOToEntity(SaleRequest saleRequest){
        return modelMapper.map(saleRequest, Sale.class);
    }

    private SaleResponse entityToDTO(Sale sale){
        return modelMapper.map(sale, SaleResponse.class);
    }
}
