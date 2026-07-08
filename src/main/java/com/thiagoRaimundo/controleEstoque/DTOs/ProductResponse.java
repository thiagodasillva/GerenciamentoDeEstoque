package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Category;
import com.thiagoRaimundo.controleEstoque.models.Lote;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {


    private Long id;
    private String name;
    private String description;
    private Long category;
    private String categoryName;
    private Collection<Lote> lotes;
    private Boolean status = true;


}
