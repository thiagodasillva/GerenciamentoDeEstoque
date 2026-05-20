package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemRequest;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleResponse;
import com.thiagoRaimundo.controleEstoque.exceptions.ResourceNotFoundException;
import com.thiagoRaimundo.controleEstoque.models.*;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleRequest;
import com.thiagoRaimundo.controleEstoque.models.Enum.TipoStockMoviment;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import com.thiagoRaimundo.controleEstoque.repository.SaleItemRepository;
import com.thiagoRaimundo.controleEstoque.repository.SaleRepository;
import com.thiagoRaimundo.controleEstoque.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.grammars.hql.HqlParser;
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

    public SaleResponse getSaleById(Long idSale){
        Sale sale = saleRepository.findByIdAndStatusTrue(idSale).orElseThrow(()-> new ResourceNotFoundException("Venda não Encontrada. ID: "+idSale));
        return entityToDTO(sale);
    }

    public List<SaleResponse> getSales(){
        return saleRepository.findByStatusTrue().stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    // buscar por periodo
    public List<SaleResponse> getSalesPerPeriods(LocalDateTime inicio, LocalDateTime fim){
        return saleRepository.findAllByDataVendaBetween(inicio,fim).stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    // buscar por margem de valor da venda
    public List<SaleResponse> getValueMargin(BigDecimal intialMargin, BigDecimal finalMargin){
        return saleRepository.findAllByValorTotalBetween(intialMargin, finalMargin).stream().map(this::entityToDTO).collect(Collectors.toList());
    }


    public void deleteLogico(Long idSale, Long idUser){

        User user = userRepository.findByIdAndStatusTrue(idUser).orElseThrow(() -> new ResourceNotFoundException("User informado não foi encontrado. ID: " + idUser));

        Sale sale = saleRepository.findByIdAndStatusTrue(idSale).orElseThrow(()-> new ResourceNotFoundException("Venda não Encontrada. ID: "+idSale));
        sale.setStatus(false);
        sale.setDeleteBy(user.getEmail());
        sale.setDaleteAt(LocalDateTime.now());

        saleRepository.save(sale);

    }

    //creatCorreto
    @Transactional
    public SaleResponse realizarVendaFEFO(Long idUser, SaleRequest dto) {

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

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

    @Transactional
    public SaleResponse realizarVenda(Long idUser, SaleRequest dto) {

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Sale sale = new Sale();
        sale.setUser(user);
        sale.setDataVenda(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (SaleItemRequest itemDTO : dto.getItens()) {

            Product product = productRepository.findById(itemDTO.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado. ID: "+ itemDTO.getProduct().getId()));

            stockMovimentService.consumoItens(product.getId(), itemDTO.getQuantidade(),idUser, TipoStockMoviment.VENDA );

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


    @Transactional
    public SaleResponse updateSale(Long idSale, SaleRequest saleRequest,Long idUser){

        Sale sale = saleRepository.findByIdAndStatusTrue(idSale).orElseThrow(()-> new ResourceNotFoundException("A compra informada não existe. ID: "+ idSale));

        if(!userRepository.existsById(idUser)){
            throw new ResourceNotFoundException("o usuario Informado não existe. ID: "+ saleRequest.getUser().getId());
        }

        sale.setDataVenda(saleRequest.getDataVenda());
        sale.setUser(saleRequest.getUser());
        sale.setValorTotal(saleRequest.getValorTotal());
        sale.getItens().clear();

        saleRepository.save(sale);

        processaNovosItens(sale,saleRequest,idUser);

        return entityToDTO(sale);

    }

    @Transactional
    private void processaNovosItens(Sale sale, SaleRequest saleRequest,Long idUser){

        Map<Long, SaleItem> itensAtuais = sale.getItens().stream()
                .collect(Collectors.toMap( i -> i.getProduct().getId(), i -> i));

        Map<Long, Integer> novosItens = saleRequest.getItens().stream()
                .collect(Collectors.toMap(i -> i.getProduct().getId(), i -> i.getQuantidade()));


        itensAtuais.forEach((productId, saleItem) -> {
            if (!novosItens.containsKey(productId)) {
                stockMovimentService.devolverProduto(productId,saleItem.getQuantidade(),idUser);
                sale.getItens().remove(saleItem);
            }
        });

        List<SaleItem> itensParaSalvar = new ArrayList<>();

        for (SaleItemRequest itemRequest : saleRequest.getItens()) {
            Long productId = itemRequest.getProduct().getId();

            Product product = productRepository.findByIdAndStatusTrue(productId).orElseThrow(() -> new ResourceNotFoundException("Produto não existe. ID: " + productId));

            if (itensAtuais.containsKey(productId)) {
                SaleItem itemExistente = itensAtuais.get(productId);
                int diferenca = itemRequest.getQuantidade() - itemExistente.getQuantidade();

                if (diferenca > 0) {
                    // Consumir mais estoque
                    stockMovimentService.consumoItens(productId,diferenca,idUser,TipoStockMoviment.VENDA);
                } else if (diferenca < 0) {
                    // Devolver estoque
                    stockMovimentService.devolverProduto(productId, Math.abs(diferenca), idUser);
                }

                itemExistente.setQuantidade(itemRequest.getQuantidade());
                itemExistente.setValorVenda(itemRequest.getValorVenda());
                itemExistente.setSubTotal(itemRequest.getSubTotal());
                itensParaSalvar.add(itemExistente);

            } else {

                stockMovimentService.consumoItens(productId, itemRequest.getQuantidade(), idUser,TipoStockMoviment.VENDA);

                SaleItem novoItem = new SaleItem();
                novoItem.setProduct(product);
                novoItem.setQuantidade(itemRequest.getQuantidade());
                novoItem.setValorVenda(itemRequest.getValorVenda());
                novoItem.setSubTotal(itemRequest.getSubTotal());
                novoItem.setSale(sale);

                itensParaSalvar.add(novoItem);
                sale.getItens().add(novoItem);
            }
        }
        saleItemRepository.saveAll(itensParaSalvar);
    }


    public BigDecimal valorVendasPeriodo(LocalDateTime inicio, LocalDateTime fim){
        return saleRepository.findAllByDataVendaBetween(inicio,fim).stream().map(Sale::getValorTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
    }


    private Sale DTOToEntity(SaleRequest saleRequest){
        return modelMapper.map(saleRequest, Sale.class);
    }

    private SaleResponse entityToDTO(Sale sale){
        return modelMapper.map(sale, SaleResponse.class);
    }
}
