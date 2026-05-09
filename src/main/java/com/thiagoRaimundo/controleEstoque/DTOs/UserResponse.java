package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Enum.TipoUser;
import com.thiagoRaimundo.controleEstoque.models.StockMovement;
import jakarta.persistence.ManyToOne;

import java.util.Collection;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private TipoUser tipoUser;
    private Collection<StockMovement> stockMovement;

    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email, TipoUser tipoUser, Collection<StockMovement> stockMovement) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tipoUser = tipoUser;
        this.stockMovement = stockMovement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TipoUser getTipoUser() {
        return tipoUser;
    }

    public void setTipoUser(TipoUser tipoUser) {
        this.tipoUser = tipoUser;
    }

    public Collection<StockMovement> getStockMovement() {
        return stockMovement;
    }

    public void setStockMovement(Collection<StockMovement> stockMovement) {
        this.stockMovement = stockMovement;
    }
}
