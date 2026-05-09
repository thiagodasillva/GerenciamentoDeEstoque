package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Enum.TipoUser;
import com.thiagoRaimundo.controleEstoque.models.StockMovement;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.Collection;

public class UserRequest {

    @NotNull(message = "O nome do usuario deve ser informado")
    @Size(max = 100)
    private String name;

    @NotNull(message = "O email do usuario deve ser informado")
    @Size(max = 100)
    @Email(message = "o formato do email deve ser valido")
    private String email;

    @NotNull(message = "a senha deve ser informada")
    @Size(max = 10,min = 6, message = "a senha deve ter valor maximo de 10 caracteres e minimo de 6")
    private String password;

    private Collection<StockMovement> stockMovement;


    @NotNull
    private TipoUser tipoUser;

    public UserRequest() {
    }

    public UserRequest(String name, String email, String password, Collection<StockMovement> stockMovement, TipoUser tipoUser) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.stockMovement = stockMovement;
        this.tipoUser = tipoUser;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
