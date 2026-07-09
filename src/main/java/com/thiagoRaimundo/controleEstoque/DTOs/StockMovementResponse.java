package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Enum.TipoStockMoviment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockMovementResponse {

    private Long id;
    private TipoStockMoviment tipo;
    private Integer quantidade; // quantidade de produtos na operação
    private String observacao;
    private LocalDateTime dataHora;
    private Long userId;
    private String userName ;
    private Long loteId;
    private String loteCodigo;
    private Long productId;
    private String productName;


}


