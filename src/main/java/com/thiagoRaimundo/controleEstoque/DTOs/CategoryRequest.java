package com.thiagoRaimundo.controleEstoque.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {


    @NotBlank(message = "O nome da categoria deve ser informado")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String name;

    @NotBlank(message = "A descrição da categoria deve ser informada")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String description;
}
