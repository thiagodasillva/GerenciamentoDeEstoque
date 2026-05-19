package com.thiagoRaimundo.controleEstoque.repository;

import com.thiagoRaimundo.controleEstoque.models.Sale;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale,Long> {

    Optional<Sale> findByIdAndStatusTrue(Long id);

    List<Sale> findByStatusTrue();

    List<Sale> findAllByDataVendaBeetwen(LocalDateTime inicio, LocalDateTime fim);

    List<Sale> findAllByValorTotalBeetwen(BigDecimal valorInicial, BigDecimal valorFinal);
}
