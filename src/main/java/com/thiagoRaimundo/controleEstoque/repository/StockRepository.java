package com.thiagoRaimundo.controleEstoque.repository;

import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock,Long> {
    Optional<Stock> findByProductId(Long id);

    Optional<Stock> findByIdAndStatusTrue(Long id);

    //@Query(value = "SELECT SUM(s.quantidade), p.nome FROM tb_sale s JOIN tb_stock p ON p.id = s.product_id WHERE p.status = false OR p.status = true GROUP BY p.nome;",nativeQuery = true)
    //List<Object[]> relatorioDeEstoquesHistorico();
}
