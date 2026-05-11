package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Sale;
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
    @Min(value = 1,message = "A Quantidade do item deve ser maior que 0")
    private Integer quantidade;


    @NotNull
    private Product product;

    @NotNull
    private Sale sale;

    public SaleItemRequest() {
    }

    public SaleItemRequest(BigDecimal valorVenda, BigDecimal subTotal, Integer quantidade, Product product, Sale sale) {
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }
}
