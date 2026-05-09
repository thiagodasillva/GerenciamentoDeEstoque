package com.thiagoRaimundo.controleEstoque.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

public class SaleRequest {

    @NotNull(message = "Informar a data é obrigatorio")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataVenda;

    @NotNull(message = "o valor total da venda deve ser informado")
    @DecimalMin(value = "0.0", message = "o valor da venda deve ser maior que zero")
    private BigDecimal valorTotal;

    @NotNull(message = "Deve ser informado um usuario")
    private Long user;
    private Collection<SaleItemRequest> itens;


    public SaleRequest() {
    }

    public SaleRequest(LocalDateTime dataVenda, BigDecimal valorTotal, Long user, Collection<SaleItemRequest> itens) {
        this.dataVenda = dataVenda;
        this.valorTotal = valorTotal;
        this.user = user;
        this.itens = itens;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Collection<SaleItemRequest> getItens() {
        return itens;
    }

    public void setItens(Collection<SaleItemRequest> itens) {
        this.itens = itens;
    }
}
