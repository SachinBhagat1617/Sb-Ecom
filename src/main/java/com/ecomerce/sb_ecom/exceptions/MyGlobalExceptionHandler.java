package com.ecomerce.sb_ecom.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // speciallised version of ControllerAdvice used for centralised error handling
// converts exception into json automatically
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) // intercepts MethodArgumentNotValidException class
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err->{
            //(FieldError)err.getField // gives error since '.' has higher priority so err without typecasting to FieldError tries to aceess .getField()
            String fieldName=((FieldError)err).getField(); // typeCast to (FieldError)
            String message=err.getDefaultMessage();
            response.put(fieldName,message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class) // custom class which we create to throw exception
    // this exceptionHandler will interpret the class ResourceNotFoundException after setting the arguments
    public ResponseEntity<String> myResourceNotFoundException(ResourceNotFoundException e){
        String message=e.getMessage();
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
    @ExceptionHandler(APIException.class)
    public ResponseEntity<String> myAPIException(APIException e){
        String message=e.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

}
