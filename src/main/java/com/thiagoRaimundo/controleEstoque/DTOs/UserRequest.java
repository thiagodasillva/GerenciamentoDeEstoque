package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Enum.TipoUser;
import com.thiagoRaimundo.controleEstoque.models.StockMovement;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotNull(message = "O nome do usuario deve ser informado")
    @Size(max = 100)
    private String name;

    @NotNull(message = "O email do usuario deve ser informado")
    @Size(max = 100)
    @Email(message = "o formato do email deve ser valido")
    private String email;

    @NotNull(message = "a senha deve ser informada")
    @Size(max = 20,min = 8, message = "a senha deve ter entre 8 e 20 caracteris")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial")
    private String password;

    @NotNull(message = "O tipo de usuário deve ser informado")
    private TipoUser tipoUser;
    private Collection<StockMovement> stockMovement;



}
