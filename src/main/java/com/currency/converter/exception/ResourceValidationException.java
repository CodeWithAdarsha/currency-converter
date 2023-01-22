package com.currency.converter.exception;

public class ResourceValidationException extends RuntimeException {

    public ResourceValidationException(String message) {
        super(message);
    }

    public ResourceValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
