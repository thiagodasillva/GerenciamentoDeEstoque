package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Lote;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Collection;

public class ProductRequest {

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    @Size(max = 500)
    private String Description;
    @NotNull
    private Long category;

    private Collection<Lote> lotes;

    public ProductRequest() {
    }

    public ProductRequest(String name, String description, Long category, Collection<Lote> lotes) {
        this.name = name;
        Description = description;
        this.category = category;
        this.lotes = lotes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public Collection<Lote> getLotes() {
        return lotes;
    }

    public void setLotes(Collection<Lote> lotes) {
        this.lotes = lotes;
    }
}
