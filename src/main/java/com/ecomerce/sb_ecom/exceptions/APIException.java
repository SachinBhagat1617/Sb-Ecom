package com.ecomerce.sb_ecom.exceptions;

public class APIException extends RuntimeException {
    public APIException() {
    }
    public APIException(String message) {
        super(message);
    }
}
