package com.app.foodcart.exceptions;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDataException extends RuntimeException {

    private final List<FieldError> fieldErrors = new ArrayList<>();

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, List<FieldError> fieldErrors) {
        super(message);
        if (fieldErrors != null) {
            this.fieldErrors.addAll(fieldErrors);
        }
    }

    public void addFieldError(String field, String message) {
        fieldErrors.add(new FieldError(field, message));
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public static class FieldError {
        private final String field;
        private final String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}