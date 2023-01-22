package com.currency.converter.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class APIExceptionHandler {

    static String validateErrorValue(String errorVal) {

        return errorVal.matches(".*\"") ? "Validation Failed : Please contact Helpdesk" : errorVal;
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage entityExistsException(ResourceValidationException e) {
        return new ErrorMessage(e.getMessage(), HttpStatus.CONFLICT, null, LocalDateTime.now());
    }

    @ExceptionHandler(value = {ResourceValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage resourceNotFoundException(ResourceValidationException e) {
        return new ErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST, null, LocalDateTime.now());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        var allErrors = ex.getBindingResult().getAllErrors();

        Map<String, String> errors = new HashMap<>();
        allErrors.forEach(
                (error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        String errorMsg =
                errors.entrySet().stream()
                        .map(e -> e.getKey().toUpperCase() + " -> " + validateErrorValue(e.getValue()))
                        .collect(Collectors.joining(","));

        return new ErrorMessage(errorMsg, HttpStatus.BAD_REQUEST, null, LocalDateTime.now());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorMessage handleConstraintViolationException(Exception ex, WebRequest request) {

        List<String> details = new ArrayList<String>();
        details.add(ex.getMessage());

        return new ErrorMessage(
                "Constraint Violations", HttpStatus.BAD_REQUEST, details, LocalDateTime.now());
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorMessage handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException ex, WebRequest request) {
        var requestContextPath = request.getContextPath().toLowerCase();
        log.error("request context >>>> : {}", requestContextPath);
        return new ErrorMessage(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, null, LocalDateTime.now());
    }
}
