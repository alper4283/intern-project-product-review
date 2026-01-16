package com.alper.product_review_backend.config;

import com.alper.product_review_backend.dto.ApiError;
import com.alper.product_review_backend.exception.UserAlreadyExistsException;
import com.alper.product_review_backend.exception.ValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiError baseError(HttpStatus status, String message, String path, List<String> details) {
        return new ApiError(
                Instant.now(),
                status.value(),
                status.name(),
                message,
                path,
                details
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex,
                                                         HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError error = baseError(
                status,
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex,
                                                          HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError error = baseError(
                status,
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError error = baseError(
                status,
                "Validation failed",
                request.getRequestURI(),
                details
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
                                                              HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.toList());

        ApiError error = baseError(
                status,
                "Validation failed",
                request.getRequestURI(),
                details
        );
        return new ResponseEntity<>(error, status);
    }

    // Generic fallback - last resort
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex,
                                                  HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError error = baseError(
                status,
                "Unexpected error",
                request.getRequestURI(),
                List.of(ex.getClass().getSimpleName())
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex,
                                                              HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError error = baseError(
                status,
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExists(UserAlreadyExistsException ex,
                                                            HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiError error = baseError(
                status,
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex,
                                                         HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ApiError error = baseError(
                status,
                "Invalid username or password",
                request.getRequestURI(),
                List.of()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex,
                                                                  HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ApiError error = baseError(
                status,
                "Authentication failed",
                request.getRequestURI(),
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex,
                                                       HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ApiError error = baseError(
                status,
                "Access denied. Insufficient permissions.",
                request.getRequestURI(),
                List.of()
        );
        return new ResponseEntity<>(error, status);
    }
}
