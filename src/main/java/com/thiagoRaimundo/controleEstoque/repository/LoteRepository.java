package com.thiagoRaimundo.controleEstoque.repository;

import com.thiagoRaimundo.controleEstoque.models.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote,Long> {

    //lista lotes ordenando por proximidade da dada de validade filtrando por produto
    List<Lote> findByProductIdOrderByValidateAsc(Long productId);

    //lista lotes ativos ordenando por proximidade da dada de validade filtrando por produto
    List<Lote> findByProductIdAndStatusTrueOrderByValidateAsc(Long productId);

    // filtra lotes ordenando por proximidade da dada de validade
    List<Lote> findByStatusTrueOrderByValidateAsc();

    //retorna todos os lotes com status true
    List<Lote> findByStatusTrue();

    //retorna lote por ID com com status true
    Optional<Lote> findByIdAndStatusTrue(Long productId);

    Optional<Lote> findByCodigoAndStatusTrue(String codigo);

    Boolean existsByCodigo(String codigo);

    Optional<Lote> findByValidate(LocalDate validate);


    //Page<Lote> findByStatusTrue(Pageable pageable);
}
