package com.thiagoRaimundo.controleEstoque.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tb_lote")
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;
    @Column(name = "quant_produtos")
    private Integer quantProdutos;

    @Column(name = "codigo", unique = true)
    private String codigo;

    @Column(name = "validate")
    private LocalDate validate;
    private Boolean status = true;

    public Lote(Long id, Product product, Integer quantProdutos, String codigo, LocalDate validate, Boolean status) {
        this.id = id;
        this.product = product;
        this.quantProdutos = quantProdutos;
        this.codigo = codigo;
        this.validate = validate;
        this.status = status;
    }

    public Lote() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantProdutos() {
        return quantProdutos;
    }

    public void setQuantProdutos(Integer quantProdutos) {
        this.quantProdutos = quantProdutos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
                "id=" + id +
                ", quantProdutos=" + quantProdutos +
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