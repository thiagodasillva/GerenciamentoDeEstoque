package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.Sale;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.math.BigDecimal;

public class SaleItemResponse {

    private Long id;
    private BigDecimal valorVenda;
    private BigDecimal subTotal;
    private Integer quantidade;
    private Product product;
    private Sale sale;

    public SaleItemResponse() {
    }

    public SaleItemResponse(Long id, BigDecimal valorVenda, BigDecimal subTotal, Integer quantidade, Product product, Sale sale) {
        this.id = id;
        this.valorVenda = valorVenda;
        this.subTotal = subTotal;
        this.quantidade = quantidade;
        this.product = product;
        this.sale = sale;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
