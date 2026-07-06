package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Sale;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleItemRequest {

    @NotNull
    @DecimalMin(value = "0.0",message = "O valor da venda do produto deve ser maior que zero")
    private BigDecimal valorVenda;

    @NotNull
    @DecimalMin(value = "0.0",message = "O valor total da venda deve ser maior que zero")
    private BigDecimal subTotal;

    @NotNull
    @Min(value = 1,message = "A Quantidade do item deve ser maior que 0")
    private Integer quantidade;


    @NotNull
    private Long productId;

    @NotNull
    private Long saleId;

}
