package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Enum.TipoStockMoviment;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMoevementRequest {

    @NotNull(message = "O tipo de movimentação deve ser informado")
    private TipoStockMoviment tipo;

    @NotNull(message = "a quantidade de produtos da operação deve ser informado")
    @Min(value = 1, message = "o valor minimo de produtos movimentados deve ser 1")
    private Integer quantidade; // quantidade de produtos na operação

    @Size(max = 200, message = "A observação deve ter no máximo 200 caracteres")
    private String observacao;

    @NotNull(message = "O lote que foi movimentado deve ser informado")
    private Long loteId;

    @NotNull(message = "O produto que voi movimentado deve ser informado")
    private Long productId;

    @NotNull(message = "O ID do usuário deve ser informado")
    private long userId;


}
