package com.thiagoRaimundo.controleEstoque.exceptions;

public class MismatchTypeException extends RuntimeException{

    public MismatchTypeException(){
        super();
    }

    public MismatchTypeException(String s){
        super(s);
    }

}
