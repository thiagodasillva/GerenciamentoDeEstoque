package com.thiagoRaimundo.controleEstoque.exceptions;

public class InsufficientStock extends RuntimeException{

    public InsufficientStock (String s){
        super(s);
    }
}
