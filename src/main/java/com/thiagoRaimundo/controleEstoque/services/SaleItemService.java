package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.SaleItem;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import com.thiagoRaimundo.controleEstoque.repository.SaleItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleItemService{

    private SaleItemRepository itemRepository;
    private ProductRepository productRepository;
    private ModelMapper modelMapper;




    public SaleItemService(SaleItemRepository itemRepository, ProductRepository productRepository) {
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
    }

    public SaleItemResponse creatItem(SaleItemRequest itemRequest){

        if(!productRepository.existsByIdAndStatusTrue(itemRequest.getProduct().getId())){
            throw new ResourceNotFoundException("Producto correspondente a este item não existe");
        }
        SaleItem saleItem = itemRepository.save(DTOToEntity(itemRequest));
        return entityToDTO(saleItem);
    }

    public List<SaleItemResponse> getItens(){
        return itemRepository.findAll().stream().map(this::entityToDTO).toList();

    }
    public SaleItemResponse getItemID(Long idSaleItem){
        SaleItem saleItem = itemRepository.findById(idSaleItem).orElseThrow(()-> new ResourceNotFoundException("O Item não existe. ID: "+ idSaleItem));
        return entityToDTO(saleItem);
    }

    public List<SaleItemResponse> getItensByProductId(Long idProduct){

        if(!productRepository.existsByIdAndStatusTrue(idProduct)){
            throw new ResourceNotFoundException("O produto informado não existe. ID: "+ idProduct);
        }

        return itemRepository.findByProductId(idProduct).stream().map(this::entityToDTO).toList();
    }

    public SaleItemResponse updateSaleItem(Long idSaleItem, SaleItemRequest saleItemRequest){

        SaleItem saleItem = itemRepository.findById(idSaleItem).orElseThrow(()-> new ResourceNotFoundException("O lote informado não existe. ID: "+idSaleItem));

        if(!productRepository.existsByIdAndStatusTrue(saleItemRequest.getProduct().getId())){
            throw  new ResourceNotFoundException("O produto informado não existe. ID: "+saleItemRequest.getProduct().getId());
        }

        saleItem.setSale(saleItemRequest.getSale());
        saleItem.setQuantidade(saleItemRequest.getQuantidade());
        saleItem.setSubTotal(saleItemRequest.getSubTotal());
        saleItem.setValorVenda(saleItemRequest.getValorVenda());
        saleItem.setProduct(saleItemRequest.getProduct());

        itemRepository.save(saleItem);
        return entityToDTO(saleItem);

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




}

