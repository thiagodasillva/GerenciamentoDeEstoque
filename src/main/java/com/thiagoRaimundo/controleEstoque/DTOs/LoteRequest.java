package com.thiagoRaimundo.controleEstoque.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thiagoRaimundo.controleEstoque.models.Product;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteRequest {


    @NotNull(message = "O produto a que o lote pertence deve ser informado")
    private Long productId;
    @NotNull(message = "a quantidade de itens do lote deve ser informada")
    @Min(value = 1, message = "um lote não pode ter quatidades de produtos nulas ou negativas")
    private Integer quantProdutos;
    @NotBlank(message = "Deve ser informado o codigo do LOTE")
    @Size(max = 100)
    private String codigo;
    @Future(message = "A data de validade deve ser futura")
    @NotNull(message = "Informar a data é obrigatória")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate validate;


}