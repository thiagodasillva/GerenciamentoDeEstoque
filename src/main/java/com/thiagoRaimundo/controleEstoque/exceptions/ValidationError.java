package com.thiagoRaimundo.controleEstoque.exceptions;

public class ValidationError extends RuntimeException{

    private String field;
    private String message;

    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
