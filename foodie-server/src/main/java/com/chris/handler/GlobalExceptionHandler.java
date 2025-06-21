package com.chris.handler;

import com.chris.exception.CustomizedBaseException;
import com.chris.vo.Result;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

/**
 * Global exception handler that catches various exceptions thrown by Controllers or Services
 * and wraps them into a unified Result format for clients.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Thrown when JSR-380 parameter validation fails (e.g., @Valid on a request body).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return Result.error(400, "Validation failed: " + errorMsg);
    }

    /**
     * Thrown when method-level validation (e.g., @Valid on method parameters) fails.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMsg = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return Result.error(400, "Validation failed: " + errorMsg);
    }

    /**
     * Handles custom business exceptions.
     */
    @ExceptionHandler(CustomizedBaseException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> handleCustomBusiness(CustomizedBaseException ex) {
        // code is provided as default, message by ex.getMessage()
        return Result.error(ex.getMessage());
    }

    /**
     * Handles JSON parsing errors, such as invalid JSON or type mismatch.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return Result.error(400, "Malformed JSON request");
    }


    /**
     * Handles all other uncaught exceptions.
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleAll(Throwable ex) {
        log.error("Unhandled exception: ", ex);
        return Result.error(500, "Internal server error");
    }

    /**
     * Handles SQL integrity constraint violations.
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> handleSQLConstraint(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")) {
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + " already exists";
            return Result.error(msg);
        } else {
            return Result.error("Unknown database error");
        }
    }
}
