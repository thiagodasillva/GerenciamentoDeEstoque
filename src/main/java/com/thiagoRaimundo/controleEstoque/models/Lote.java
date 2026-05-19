package com.thiagoRaimundo.controleEstoque.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "tb_lote")
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Product product;
    private Integer quantAtual;
    private LocalDate validate;
    private Boolean status = true;

    public Lote(Product product, Integer quantAtual, LocalDate validate) {
        this.product = product;
        this.quantAtual = quantAtual;
        this.validate = validate;
    }

    public Lote() {
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantAtual() {
        return quantAtual;
    }

    public void setQuantAtual(Integer quantAtual) {
        this.quantAtual = quantAtual;
    }

    public LocalDate getValidate() {
        return validate;
    }

    public void setValidate(LocalDate validate) {
        this.validate = validate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Lote{" +
                "produto=" + product +
                ", quantAtual=" + quantAtual +
                ", validate=" + validate +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lote lote = (Lote) o;
        return Objects.equals(id, lote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
