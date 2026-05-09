package com.thiagoRaimundo.controleEstoque.DTOs;

import com.thiagoRaimundo.controleEstoque.models.Category;
import com.thiagoRaimundo.controleEstoque.models.Lote;
import jakarta.persistence.*;

import java.util.Collection;

public class ProductResponse {


    private Long id;

    private String name;

    private String Description;

    private Category category;
    private Collection<Lote> lotes;

    private Boolean status = true;

    public ProductResponse() {
    }

    public ProductResponse(Long id, String name, String description, Category category, Collection<Lote> lotes, Boolean status) {
        this.id = id;
        this.name = name;
        Description = description;
        this.category = category;
        this.lotes = lotes;
        this.status = status;
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Collection<Lote> getLotes() {
        return lotes;
    }

    public void setLotes(Collection<Lote> lotes) {
        this.lotes = lotes;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
