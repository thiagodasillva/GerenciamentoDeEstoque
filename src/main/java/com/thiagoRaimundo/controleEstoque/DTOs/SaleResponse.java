package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.SaleItem;
import com.thiagoRaimundo.controleEstoque.models.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

public class SaleResponse {
    private Long id;
    private LocalDateTime dataVenda;
    private BigDecimal valorTotal;
    private User user;
    private Collection<SaleItem> itens;


    public SaleResponse() {
    }

    public SaleResponse(Long id, LocalDateTime dataVenda, BigDecimal valorTotal, User user, Collection<SaleItem> itens) {
        this.id = id;
        this.dataVenda = dataVenda;
        this.valorTotal = valorTotal;
        this.user = user;
        this.itens = itens;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<SaleItem> getItens() {
        return itens;
    }

    public void setItens(Collection<SaleItem> itens) {
        this.itens = itens;
    }
}
