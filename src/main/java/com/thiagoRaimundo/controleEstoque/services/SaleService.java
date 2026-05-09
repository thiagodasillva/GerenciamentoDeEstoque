package com.thiagoRaimundo.controleEstoque.services;

import com.thiagoRaimundo.controleEstoque.DTOs.SaleItemRequest;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Sale;
import com.thiagoRaimundo.controleEstoque.DTOs.SaleRequest;
import com.thiagoRaimundo.controleEstoque.models.SaleItem;
import com.thiagoRaimundo.controleEstoque.models.User;
import com.thiagoRaimundo.controleEstoque.repository.ProductRepository;
import com.thiagoRaimundo.controleEstoque.repository.SaleRepository;
import com.thiagoRaimundo.controleEstoque.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SaleService {

    private SaleRepository saleRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private StockMovimentService stockMovimentService;

    public SaleService(SaleRepository saleRepository, UserRepository userRepository, ProductRepository productRepository, StockMovimentService stockMovimentService) {
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockMovimentService = stockMovimentService;
    }



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
}
