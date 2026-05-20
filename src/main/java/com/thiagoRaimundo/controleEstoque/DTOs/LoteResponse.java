package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Product;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteResponse {

    private Long id;
    private Product product;
    private Integer quantProdutos;
    private String codigo;
    private LocalDate validate;
    private Boolean status = true;


}
