package com.custempmanag.marketing.exception;

import com.custempmanag.marketing.response.MessageResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
class GlobalExceptionHandler {

    @Autowired
    private final MessageSource messageSource;

    GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Database constraint violation: " + ex.getRootCause().getMessage());
        MessageResponse response = new MessageResponse(
                HttpStatus.BAD_REQUEST.toString(),
                messageSource.getMessage("internal.error", null, LocaleContextHolder.getLocale()), // errors.toString()
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        MessageResponse response = new MessageResponse(
                HttpStatus.BAD_REQUEST.toString(), // status
                messageSource.getMessage("validation.failed", null, LocaleContextHolder.getLocale()), // errors.toString(), // message
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

    @ExceptionHandler(DenyAccessException.class)
    public ResponseEntity<?> handleDenyAccessException(DenyAccessException ex) {
        MessageResponse response = new MessageResponse(
                HttpStatus.UNAUTHORIZED.toString(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException ex) {
        MessageResponse response = new MessageResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                messageSource.getMessage("internal.error", null, LocaleContextHolder.getLocale()),
                null
                // NEED TO MODIFY THIS TO MAKE RETURN FAILED TO UPLOAD PHOTO MESSAGE NOT INTERNAL SERVER ERROR MESSAGE
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
                messageSource.getMessage("internal.error", null, LocaleContextHolder.getLocale()), // errors.toString()
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
                messageSource.getMessage("internal.error", null, LocaleContextHolder.getLocale()), // errors.toString()
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
                messageSource.getMessage("internal.error", null, LocaleContextHolder.getLocale()), // errors.toString()
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
                messageSource.getMessage("internal.error", null, LocaleContextHolder.getLocale()), // errors.toString()
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
