package com.thiagoRaimundo.controleEstoque.exceptions;

import com.thiagoRaimundo.controleEstoque.commons.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    // Falta construit os tratamentos das outras exceptions não genericas
    private boolean enableTrace = true;


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"erro interno");
        if(enableTrace){
            errorResponse.setStackTrace(Utils.getStackTrace(ex));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
       // return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }



    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage());

        if(enableTrace){
            errorResponse.setStackTrace(Utils.getStackTrace(ex));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        //return buildErrorResponse(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(LoteNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleLoteNotFoundException(LoteNotFoundException ex, WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage());

        if(enableTrace){
            errorResponse.setStackTrace(Utils.getStackTrace(ex));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        //return buildErrorResponse(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(StockNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleStockNotFoundException(StockNotFoundException ex, WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage());

        if(enableTrace){
            errorResponse.setStackTrace(Utils.getStackTrace(ex));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        //return buildErrorResponse(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage());

        if(enableTrace){
            errorResponse.setStackTrace(Utils.getStackTrace(ex));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        //return buildErrorResponse(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(MismatchTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handlerMismatchTypeException (MismatchTypeException ex, WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        if (enableTrace){errorResponse.setStackTrace(Utils.getStackTrace(ex));}

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(InsufficientStock.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handlerInsuficientStock (InsufficientStock ex, WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());

        if (enableTrace){errorResponse.setStackTrace(Utils.getStackTrace(ex));}

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }


    // conflito com o metodo padrão do Spring ResponseEntityExceptionHandler.handleException()
    //validação para itens com validação no @RequestBody
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, WebRequest request){
//        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),"validação nos campos de inserção de dados");
//
//        // Extrai todos os erros de campo
//        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
//                errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage())
//        );
//
//        if(enableTrace){
//            errorResponse.setStackTrace(Utils.getStackTrace(ex));
//        }
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//    }

    //validação de parâmetros de query string ou formulários
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationException(BindException ex, WebRequest request){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),"Erro de validação nos campos de inserção de dados");

        ex.getBindingResult().getFieldErrors().forEach(FieldError -> errorResponse.addValidationError(FieldError.getField(),FieldError.getDefaultMessage()));

        if(enableTrace){
            errorResponse.setStackTrace(Utils.getStackTrace(ex));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }






    private ResponseEntity<Object> buildErrorResponse(Exception exception,
                                                      HttpStatus httpStatus,
                                                      WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                exception.getMessage()
        );

        if(enableTrace) errorResponse.setStackTrace(Utils.getStackTrace(exception));
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

}
