package com.thiagoRaimundo.controleEstoque.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioTipoMovimentoDTO {

    private String tipo;
    private Integer quantidadeTotal;
    private Double percentual;

}
