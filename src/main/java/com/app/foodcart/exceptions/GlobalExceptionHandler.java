package com.app.foodcart.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@SuppressWarnings("all")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Handle Resource Not Found Exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "Resource Not Found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Handle Duplicate Resource Exception
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                "Duplicate Resource");
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle Bad Request Exception
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Bad Request");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle IllegalArgumentException (often from invalid Base64 encoding)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        String message = ex.getMessage();
        if (message.contains("Base64")) {
            message = "Invalid image format: The image must be a valid Base64 encoded string";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                "Invalid Input");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle Invalid Data Exception
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataException(InvalidDataException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Invalid Data");

        // Add all field errors to the response
        ex.getFieldErrors().forEach(fieldError -> {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getMessage());
        });

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle Incorrect Result Size Data Access Exception (when a query returns
    // multiple results when expecting one)
    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectResultSizeDataAccessException(
            IncorrectResultSizeDataAccessException ex) {
        String message = "A query expected to return a unique result returned multiple results";

        // Extract more specific information if available
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("query did not return a unique result")) {
                message = "Found multiple records when expecting a unique result. This usually happens when there are duplicate entries in the database.";
            } else {
                message = ex.getMessage();
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                message,
                "Data Integrity Violation");

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle Unauthorized Exception
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                "Unauthorized");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // Handle Forbidden Exception
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                "Forbidden");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Handle Constraint Violation Exception (validation errors)
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            jakarta.validation.ConstraintViolationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error",
                "Constraint Violation");

        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errorResponse.addValidationError(fieldName, message);
        });

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle Method Argument Type Mismatch Exception
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                        ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()),
                "Type Mismatch");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle Data Integrity Violation Exception (database constraints)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorResponse errorResponse;

        if (ex.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException constraintEx = (ConstraintViolationException) ex.getCause();
            String constraintName = constraintEx.getConstraintName();
            String message = "";

            // Check for unique constraint violations
            if (constraintName != null) {
                if (constraintName.toLowerCase().contains("uk_") || constraintName.toLowerCase().contains("unique")) {
                    // Extract entity and field from constraint name if possible
                    String[] parts = constraintName.split("_");
                    if (parts.length >= 3) {
                        String entity = parts[1];
                        String field = parts[2];
                        message = String.format("A %s with this %s already exists", entity, field);
                    } else {
                        message = "A record with the same unique values already exists";
                    }
                } else if (constraintName.toLowerCase().contains("fk_")
                        || constraintName.toLowerCase().contains("foreign")) {
                    message = "Referenced entity does not exist or cannot be deleted due to references";
                } else if (constraintName.toLowerCase().contains("not_null")) {
                    message = "Required field cannot be null";
                } else {
                    message = "Database constraint violation: " + constraintName;
                }
            } else {
                message = "Database constraint violation occurred";
            }

            errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    message,
                    "Data Integrity Violation");
        } else if (ex.getCause() instanceof SQLIntegrityConstraintViolationException) {
            SQLIntegrityConstraintViolationException sqlEx = (SQLIntegrityConstraintViolationException) ex.getCause();
            String sqlMessage = sqlEx.getMessage();
            String message;

            if (sqlMessage.contains("Duplicate entry")) {
                message = "Duplicate entry detected: " + sqlMessage;
            } else if (sqlMessage.contains("cannot be null")) {
                message = "Required field cannot be null: " + sqlMessage;
            } else if (sqlMessage.contains("foreign key constraint fails")) {
                message = "Referenced entity does not exist or cannot be deleted due to references";
            } else {
                message = "Database constraint violation: " + sqlMessage;
            }

            errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    message,
                    "Data Integrity Violation");
        } else {
            errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Database error occurred: " + ex.getMessage(),
                    "Data Integrity Violation");
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle Method Argument Not Valid Exception (for @Valid annotations)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error: Please check the required fields",
                "Validation Error");

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String message = error.getDefaultMessage();
            errorResponse.addValidationError(fieldName, message);
        });

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                "Internal Server Error");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}