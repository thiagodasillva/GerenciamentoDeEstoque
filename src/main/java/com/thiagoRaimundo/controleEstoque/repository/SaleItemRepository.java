package com.thiagoRaimundo.controleEstoque.repository;

import com.thiagoRaimundo.controleEstoque.models.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem,Long> {

    //busca os itens vendidos filtrados por produto
    List<SaleItem> findByProductId(Long id);
}
