package com.thiagoRaimundo.controleEstoque.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class SaleItemRequest {

    @NotNull
    @DecimalMin(value = "0.0",message = "O valor da venda do produto deve ser maior que zero")
    private BigDecimal valorVenda;

    @NotNull
    @DecimalMin(value = "0.0",message = "O valor total da venda deve ser maior que zero")
    private BigDecimal subTotal;

    @NotNull
    @Min(value = 1,message = "Uma venda deve ter ao menos um produto")
    private Integer quantidade;


    @NotNull
    private Long product;

    @NotNull
    private Long sale;

    public SaleItemRequest() {
    }

    public SaleItemRequest(BigDecimal valorVenda, BigDecimal subTotal, Integer quantidade, Long product, Long sale) {
        this.valorVenda = valorVenda;
        this.subTotal = subTotal;
        this.quantidade = quantidade;
        this.product = product;
        this.sale = sale;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    public Long getSale() {
        return sale;
    }

    public void setSale(Long sale) {
        this.sale = sale;
    }
}
