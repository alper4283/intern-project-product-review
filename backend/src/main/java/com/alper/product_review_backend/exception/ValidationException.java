package com.alper.product_review_backend.exception;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
