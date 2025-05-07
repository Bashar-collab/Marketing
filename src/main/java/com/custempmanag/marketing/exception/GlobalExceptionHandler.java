package com.custempmanag.marketing.exception;

import com.custempmanag.marketing.response.MessageResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Database constraint violation: " + ex.getRootCause().getMessage());
        MessageResponse response = new MessageResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Error occurred, please try again", // errors.toString()
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        MessageResponse response = new MessageResponse(
                HttpStatus.BAD_REQUEST.toString(), // status
                "Validation failed", // message
                errors // data
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomExceptions(CustomException ex) {

        MessageResponse response = new MessageResponse(
                HttpStatus.BAD_REQUEST.toString(), // status
                ex.getMessage(), // message
                null // data
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        MessageResponse response = new MessageResponse(
                HttpStatus.NOT_FOUND.toString(),  // status
                ex.getMessage(),  // message (custom message from exception)
                null  // data
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);  // 404 Not Found
    }

    // Handle @RequestParam, @PathVariable, and @Validated service layer validation
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });

        MessageResponse response = new MessageResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Error occurred, please try again",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle type mismatch (e.g., passing "abc" instead of a number)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MessageResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getName(), "Invalid value: " + ex.getValue() + ". Expected type: " + ex.getRequiredType().getSimpleName());
        MessageResponse response = new MessageResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Error occurred, please try again",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle missing required parameters (e.g., @RequestParam(required = true))
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<MessageResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getParameterName(), "Parameter is missing");
        MessageResponse response = new MessageResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Error occurred, please try again",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle malformed JSON (e.g., invalid JSON syntax)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Invalid JSON: " + ex.getRootCause().getMessage());
        MessageResponse response = new MessageResponse(
                HttpStatus.BAD_REQUEST.toString(),
                "Error occurred, please try again",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
