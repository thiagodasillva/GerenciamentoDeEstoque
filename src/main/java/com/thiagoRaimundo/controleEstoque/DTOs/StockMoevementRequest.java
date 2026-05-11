package com.thiagoRaimundo.controleEstoque.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thiagoRaimundo.controleEstoque.models.Enum.TipoStockMoviment;
import com.thiagoRaimundo.controleEstoque.models.Lote;
import com.thiagoRaimundo.controleEstoque.models.Product;
import com.thiagoRaimundo.controleEstoque.models.User;
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

    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "A data da movimentação deve ser informada")
    private LocalDateTime dataHora;

    @NotNull(message = "um usuario deve ser informado")
    private User user;

    @NotNull(message = "O lote que foi movimentado deve ser informado")
    private Lote lote;

    @NotNull(message = "O produto que voi movimentado deve ser informado")
    private Product product;


    public StockMoevementRequest() {
    }

    public StockMoevementRequest(TipoStockMoviment tipo, Integer quantidade, String observacao, LocalDateTime dataHora, User user, Lote lote, Product product) {
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.observacao = observacao;
        this.dataHora = dataHora;
        this.user = user;
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

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
