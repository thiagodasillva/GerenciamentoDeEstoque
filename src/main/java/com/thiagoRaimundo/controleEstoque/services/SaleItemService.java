package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Sale;
import com.thiagoRaimundo.controleEstoque.models.SaleItem;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import com.thiagoRaimundo.controleEstoque.repository.SaleItemRepository;
import com.thiagoRaimundo.controleEstoque.repository.SaleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleItemService{

    private SaleItemRepository itemRepository;
    private SaleRepository saleRepository;
    private ProductRepository productRepository;
    private ModelMapper modelMapper;

    public SaleItemService() {
    }

    public SaleItemService(SaleItemRepository itemRepository, SaleRepository saleRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.itemRepository = itemRepository;
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    public SaleItemResponse creatItem(SaleItemRequest itemRequest){

        Product product = productRepository.findByIdAndStatusTrue(itemRequest.getProductId()).orElseThrow(()-> new ResourceNotFoundException("O produto informado não existe. ID: " + itemRequest.getProductId()));
        Sale sale = saleRepository.findByIdAndStatusTrue(itemRequest.getSaleId()).orElseThrow(()-> new ResourceNotFoundException("A venda informada não existe.ID: "+ itemRequest.getSaleId()));

        SaleItem saleItem = new SaleItem();

        saleItem.setProduct(product);
        saleItem.setSale(sale);
        saleItem.setSubTotal(itemRequest.getSubTotal());
        saleItem.setQuantidade(itemRequest.getQuantidade());
        saleItem.setValorVenda(itemRequest.getValorVenda());

        SaleItem savedSaleItem = itemRepository.save(saleItem);
        return entidadeToDTO(savedSaleItem);
    }

    public List<SaleItemResponse> getItens(){
        return itemRepository.findAll().stream().map(this::entidadeToDTO).toList();

    }
    public SaleItemResponse getItemID(Long idSaleItem){
        SaleItem saleItem = itemRepository.findById(idSaleItem).orElseThrow(()-> new ResourceNotFoundException("O Item não existe. ID: "+ idSaleItem));
        return entidadeToDTO(saleItem);
    }

    public List<SaleItemResponse> getItensByProductId(Long idProduct){

        if(!productRepository.existsByIdAndStatusTrue(idProduct)){
            throw new ResourceNotFoundException("O produto informado não existe. ID: "+ idProduct);
        }

        return itemRepository.findByProductId(idProduct).stream().map(this::entidadeToDTO).toList();
    }

    public SaleItemResponse updateSaleItem(Long idSaleItem, SaleItemRequest saleItemRequest){

        SaleItem saleItem = itemRepository.findById(idSaleItem).orElseThrow(()-> new ResourceNotFoundException("O item informado não existe. ID: "+idSaleItem));
        Sale sale = saleRepository.findByIdAndStatusTrue(saleItemRequest.getProductId()).orElseThrow(()-> new ResourceNotFoundException("A venda informada não existe. ID: " + saleItemRequest.getSaleId()));
        Product product = productRepository.findByIdAndStatusTrue(saleItemRequest.getProductId()).orElseThrow(()-> new ResourceNotFoundException("O produto informado não existe. ID :" + saleItemRequest.getProductId() ));


        if(!productRepository.existsByIdAndStatusTrue(saleItemRequest.getProductId())){
            throw  new ResourceNotFoundException("O produto informado não existe. ID: "+saleItemRequest.getProductId());
        }

        saleItem.setSale(sale);
        saleItem.setQuantidade(saleItemRequest.getQuantidade());
        saleItem.setSubTotal(saleItemRequest.getSubTotal());
        saleItem.setValorVenda(saleItemRequest.getValorVenda());
        saleItem.setProduct(product);

        itemRepository.save(saleItem);
        return entidadeToDTO(saleItem);

    }


    public void delete(Long idItemSale){

        SaleItem saleItem = itemRepository.findById(idItemSale).orElseThrow(()-> new ResourceNotFoundException("O lote informado não existe. ID: "+idItemSale));
        itemRepository.delete(saleItem);

    }

    private SaleItem DTOToEntity(SaleItemRequest saleItemRequest){
        return modelMapper.map(saleItemRequest, SaleItem.class);
    }

    private SaleItemResponse entityToDTO(SaleItem saleItem){
        return modelMapper.map(saleItem, SaleItemResponse.class);
    }

    private SaleItemResponse entidadeToDTO(SaleItem saleItem){
        SaleItemResponse response= new SaleItemResponse();
        response.setId(saleItem.getId());
        response.setQuantidade(saleItem.getQuantidade());
        response.setSubTotal(saleItem.getValorVenda());
        response.setValorVenda(saleItem.getValorVenda());
        response.setProductId(saleItem.getProduct().getId());
        response.setProductName(saleItem.getProduct().getName());

        return response;
    }




}

