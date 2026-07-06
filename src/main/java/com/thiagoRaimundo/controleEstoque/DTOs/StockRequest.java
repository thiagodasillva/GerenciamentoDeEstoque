package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {

    @NotNull(message = "O produto do estoque a ser criado deve ser informado")
    private Long productId;
    @NotNull(message = "a quantidade atual ao criar o produto deve ser informado")
    @Min(value = 0, message = "A quantidade atual não pode ser negativa")
    private Integer quantidadeAtual;
    @NotNull(message = "A quantidade que o produto pode ter deve ser informada")
    @Min(value = 0, message = "A quantidade mínima não pode ser negativa")
    private Integer quantidadeMinima;
    @NotNull(message = "A quantidade máxima deve ser informada")
    @Min(value = 1, message = "A quantidade máxima deve ser pelo menos 1")
    private Integer quantidadeMaxima;


}
