package com.thiagoRaimundo.controleEstoque.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockRequest {

    @NotNull(message = "O produto do estoque a ser criado deve ser informado")
    private Long product;
    @NotNull(message = "a quantidade atual ao criar o produto deve ser informado")
    @Min(value = 0)
    private Integer quantidadeAtual;
    @NotNull(message = "A quantidade que o produto pode ter deve ser informada")
    @Min(value = 1)
    private Integer quantidadeMinima;
    private Integer quantidadeMaxima;

    public StockRequest(Long product, Integer quantidadeAtual, Integer quantidadeMinima, Integer quantidadeMaxima) {
        this.product = product;
        this.quantidadeAtual = quantidadeAtual;
        this.quantidadeMinima = quantidadeMinima;
        this.quantidadeMaxima = quantidadeMaxima;
    }

    public StockRequest() {
    }


    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    public Integer getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Integer quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public Integer getQuantidadeMaxima() {
        return quantidadeMaxima;
    }

    public void setQuantidadeMaxima(Integer quantidadeMaxima) {
        this.quantidadeMaxima = quantidadeMaxima;
    }
}
