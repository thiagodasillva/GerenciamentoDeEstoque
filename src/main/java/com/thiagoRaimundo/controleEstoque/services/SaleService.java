package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemResponse;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.*;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleRequest;
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

    /* funções da sale : realizar a venda acessando a função especifica do movimentação de estoque
        relatrio de todas as vendas, filtrada por data,
        relatorio do valor arrecadado no geral e por data especidica,
    */


    public SaleResponse getSaleById(Long idSale){
        Sale sale = saleRepository.findByIdAndStatusTrue(idSale).orElseThrow(()-> new ResourceNotFoundException("Venda não Encontrada. ID: "+idSale));
        return entityToDTO(sale);
    }

    public List<SaleResponse> getSales(){
        return saleRepository.findByStatusTrue().stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    // buscar por periodo
    public List<SaleResponse> getSalesPerPeriods(LocalDateTime inicio, LocalDateTime fim){
        return saleRepository.findAllByDataVendaBeetwen(inicio,fim).stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    // buscar por margem de valor da venda
    public List<SaleResponse> getValueMargin(BigDecimal intialMargin, BigDecimal finalMargin){
        return saleRepository.findAllByValorTotalBeetwen(intialMargin, finalMargin).stream().map(this::entityToDTO).collect(Collectors.toList());
    }


    public void delete(Long idSale){
        Sale sale = saleRepository.findByIdAndStatusTrue(idSale).orElseThrow(()-> new ResourceNotFoundException("Venda não Encontrada. ID: "+idSale));
        //incluir função para guardar dados do usuario que apagou
        sale.setStatus(false);
        saleRepository.save(sale);

    }

    //creatCorreto
    @Transactional
    public SaleResponse realizarVendaFEFO(Long idUser, SaleRequest dto) {

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Sale sale = new Sale();
        sale.setUser(user);
        sale.setDataVenda(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (SaleItemRequest itemDTO : dto.getItens()) {

            Product product = productRepository.findById(itemDTO.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado. ID: "+ itemDTO.getProduct().getId()));

            stockMovimentService.consumoItensFEFO(product.getId(), itemDTO.getQuantidade(), user);

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
        saleRepository.save(sale);

        return entityToDTO(sale);
    }


    public SaleResponse updateSale(Long idStockMovimentacso, SaleRequest saleRequest, User user){

        StockMovement stockMovement = stockMovimentService.
        if(!userRepository.existsById(user.getId())){
            throw new ResourceNotFoundException("o usuario Informado não existe. ID: "+ saleRequest.getUser().getId());
        }

        sale.setDataVenda(saleRequest.getDataVenda());
        sale.setUser(saleRequest.getUser());
        sale.setValorTotal(saleRequest.getValorTotal());
        sale.getItens().clear();

        saleRepository.save(sale);

        return entityToDTO(sale);

    }

    private void processaNovosItens(Sale sale, SaleRequest saleRequest, User user){

        Map<Long, SaleItem> itensAtuais = sale.getItens().stream() .collect(Collectors.toMap( i -> i.getProduct().getId(), i -> i));

        Map<Long, Integer> novosItens = saleRequest.getItens().stream().collect(Collectors.toMap(i -> i.getProduct().getId(), i -> i.getQuantidade()));

        Map<Long,Integer> deveDevolver = new HashMap<>();

        //produto presentes em ambas as listas
        for(Map.Entry<Long,Integer> entry : novosItens.entrySet()){
            Long idProduct = entry.getKey();
            Integer quant = entry.getValue();
            SaleItem itemAtual = itensAtuais.get(idProduct);

            if(!itensAtuais.isEmpty()){
                int diferenca = quant - itemAtual.getQuantidade();

                if (diferenca > 0){
                    stockMovimentService.consumoItensFEFO(idProduct,diferenca,user)
                } else if (diferenca < 0) {
                    deveDevolver.put(itemAtual.getId(),idProduct.intValue());
                }

                itemAtual.setQuantidade(quant);

            }

        }


        for(Map.Entry<Long,Integer> entry : novosItens.entrySet()){
            Long idProduct = entry.getKey();
            Integer quant = entry.getValue();


            if(!itensAtuais.containsKey(idProduct)){

                Product product = productRepository.findByIdAndStatusTrue(idProduct)
                        .orElseThrow(()-> new ResourceNotFoundException("Produto informado na lista não Existe. ID: "+idProduct));


                SaleItemRequest saleItemRequest = saleRequest.getItens().stream().filter(i -> i.getProduct().getId().equals(idProduct)).findFirst().orElseThrow(()-> new IllegalArgumentException("Erro no registro de um novo item da lista no metodo de atualizar compra"));

                stockMovimentService.consumoItensFEFO(idProduct,quant,user);

                SaleItem saleItem =new SaleItem();
                saleItem.setProduct(product);
                saleItem.setQuantidade(quant);
                saleItem.setValorVenda(saleItemRequest.getValorVenda());
                saleItem.setSubTotal(saleItemRequest.getSubTotal());
                saleItem.setSale(sale);

                sale.getItens().add(saleItem);
            }
        }


        for(Map.Entry<Long,SaleItem> entry: itensAtuais.entrySet()){
            Long productId = entry.getKey();
            SaleItem itemAtual = entry.getValue();

            if (!novosItens.containsKey(productId)) {
                int quantidadeDevolver = itemAtual.getQuantidade();

                deveDevolver.put(productId,quantidadeDevolver);
                sale.getItens().remove(itemAtual);
            }


        }
    }









//    public SaleResponse atualizarVenda(Long idSale, SaleRequest saleRequest){
//
//        Sale sale = saleRepository.findByIdAndStatusTrue(idSale).orElseThrow(()-> new ResourceNotFoundException("A venda informada não existe, ID:"+ idSale ));
//
//        User user = userRepository.findById(saleRequest.getUser().getId())
//                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
//        for(SaleItemRequest item: saleRequest.getItens()){
//            Product product = productRepository.findByIdAndStatusTrue(item.getProduct().getId()).orElseThrow(() -> new ResourceNotFoundException("O produto item infomrado não condiz com nenhum produto. ID: "+item.toString()));
//
//            //alterar a quantidade de itens que estão na lista em ambas as listas
//            int quant = item.getQuantidade() - (int)sale.getItens().stream().filter(p-> p.getProduct().equals(product)).count();
//
//            if(quant<0){
//                stockMovimentService.consumoFEFO(product,quant, user);
//                item.setQuantidade(item.getQuantidade()-quant);
//            } else if (quant>0) {
//                //função para retornar itens ao estoque
//                item.setQuantidade(item.getQuantidade()+quant);
//            }
//
//
//
//        }
//
//
//
//    }















    private Sale DTOToEntity(SaleRequest saleRequest){
        return modelMapper.map(saleRequest, Sale.class);
    }

    private SaleResponse entityToDTO(Sale sale){
        return modelMapper.map(sale, SaleResponse.class);
    }
}
