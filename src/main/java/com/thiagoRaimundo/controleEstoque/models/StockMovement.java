package com.thiagoRaimundo.controleEstoque.models;

import com.thiagoRaimundo.controleEstoque.models.Enum.TipoStockMoviment;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_stockMoviment")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoStockMoviment tipo;
    private Integer quantidade; // quantidade de produtos na operação
    private String observacao;
    private LocalDateTime dataHora;
    @ManyToOne
    private User user;
    @ManyToOne
    private Product product;

    public StockMovement() {
    }

    public Long getId() {
        return id;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "tipo=" + tipo +
                ", quantidade=" + quantidade +
                ", observacao='" + observacao + '\'' +
                ", dataHora=" + dataHora +
                ", user=" + user +
                ", product=" + product +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockMovement that = (StockMovement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
