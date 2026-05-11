package com.thiagoRaimundo.controleEstoque.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thiagoRaimundo.controleEstoque.models.Product;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LoteRequest {

    @NotBlank(message = "O produto a que o lote pertence deve ser informado")
    private Product product;
    @NotNull(message = "a quantidade de itens do lote deve ser informada")
    @Min(value = 1,message = "um lote não pode ter quatidades de produtos nulas ou negativas")
    private Integer quantAtual;

    @Future(message = "A data de validade deve ser futura")
    @NotNull(message = "Informar a data é obrigatória")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate validate;


    public LoteRequest() {
    }

    public LoteRequest(Product product, Integer quantAtual, LocalDate validate) {
        this.product = product;
        this.quantAtual = quantAtual;
        this.validate = validate;
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
}
