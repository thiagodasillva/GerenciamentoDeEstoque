package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Product;

import java.time.LocalDate;

public class LoteResponse {

    private Long id;
    private Product product;
    private Integer quantAtual;
    private LocalDate validate;
    private Boolean status = true;


    public LoteResponse(Long id, Product product, Integer quantAtual, LocalDate validate, Boolean status) {
        this.id = id;
        this.product = product;
        this.quantAtual = quantAtual;
        this.validate = validate;
        this.status = status;
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
}
