package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Enum.TipoStockMoviment;
import com.thiagoRaimundo.controleEstoque.models.Lote;
import com.thiagoRaimundo.controleEstoque.models.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class StockMoevementRequest {

    @NotNull(message = "O tipo de movimentação deve ser informado")
    private TipoStockMoviment tipo;

    @NotNull(message = "a quantidade de produtos da operação deve ser informado")
    @Min(value = 1, message = "o valor minimo de produtos movimentados deve ser 1")
    private Integer quantidade; // quantidade de produtos na operação

    @Size(max = 200)
    private String observacao;

    @NotNull(message = "O lote que foi movimentado deve ser informado")
    private Lote lote;

    @NotNull(message = "O produto que voi movimentado deve ser informado")
    private Product product;


    public StockMoevementRequest() {
    }

    public StockMoevementRequest(TipoStockMoviment tipo, Integer quantidade, String observacao, Lote lote, Product product) {
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.observacao = observacao;
        this.lote = lote;
        this.product = product;
    }

    public TipoStockMoviment getTipo() {
        return tipo;
    }

    public void setTipo(TipoStockMoviment tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
