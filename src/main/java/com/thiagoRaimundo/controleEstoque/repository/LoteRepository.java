package com.thiagoRaimundo.controleEstoque.repository;

import com.thiagoRaimundo.controleEstoque.models.Lote;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote,Long> {

    //lista lotes ordenando por proximidade da dada de validade filtrando por produto
    List<Lote> findByProductIdOrderByDataValidadeAsc(Long productId);

    //lista lotes ativos ordenando por proximidade da dada de validade filtrando por produto
    List<Lote> findByProductIdAndStatusTrueOrderByDataValidadeAsc(Long productId);

    // filtra lotes ordenando por proximidade da dada de validade
    List<Lote> findByStatusTrueOrderByDataValidadeAsc();

    //retorna todos os lotes com status true
    List<Lote> findByStatusTrue();

    //retorna lote por ID com com status true
    Optional<Lote> findByIdAndStatusTrue(Long productId);

    Optional<Lote> findByProdut(Long id);


    //Page<Lote> findByStatusTrue(Pageable pageable);
}
