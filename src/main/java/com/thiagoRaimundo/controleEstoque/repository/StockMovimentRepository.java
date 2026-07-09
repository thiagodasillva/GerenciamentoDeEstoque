package com.thiagoRaimundo.controleEstoque.repository;

import com.thiagoRaimundo.controleEstoque.models.Enum.TipoStockMoviment;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Stock;
import com.thiagoRaimundo.controleEstoque.models.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;


public interface StockMovimentRepository extends JpaRepository<StockMovement,Long> {

    Page<StockMovement> findByProductIdOrderByDataHoraDesc(Long id, Pageable pageable);
    Page<StockMovement> findByTipoOrderByDataHoraDesc(TipoStockMoviment tipo, Pageable pageable);
    Page<StockMovement> findByUserIdOrderByDataHoraDesc(Long id, Pageable page);
    Page<StockMovement> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

}
