package com.alper.product_review_backend.aop.aspect;

import com.alper.product_review_backend.aop.annotation.*;
import com.alper.product_review_backend.exception.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Aspect for centralized validation of method parameters using custom annotations.
 * Provides consistent validation handling across all controllers and services.
 */
@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class ValidationAspect {

    private final EntityManager entityManager;

    /**
     * Validates parameters annotated with @ValidRange.
     */
    @Before("execution(* com.alper.product_review_backend..*.*(..)) && @annotation(validateRequest)")
    public void validateMethod(JoinPoint joinPoint, ValidateRequest validateRequest) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            validateParameter(parameters[i], args[i], validateRequest.message());
        }
    }

    /**
     * Validates all controller method parameters with custom annotations.
     */
    @Before("execution(* com.alper.product_review_backend.controller..*.*(..))")
    public void validateControllerParameters(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            validateParameter(parameters[i], args[i], "Validation failed");
        }
    }

    private void validateParameter(Parameter parameter, Object value, String messagePrefix) {
        // Validate @ValidRange
        ValidRange validRange = parameter.getAnnotation(ValidRange.class);
        if (validRange != null && value instanceof Number) {
            validateRange((Number) value, validRange);
        }

        // Validate @ValidLength
        ValidLength validLength = parameter.getAnnotation(ValidLength.class);
        if (validLength != null) {
            validateLength(value, validLength);
        }

        // Validate @EntityExists
        EntityExists entityExists = parameter.getAnnotation(EntityExists.class);
        if (entityExists != null && value != null) {
            validateEntityExists(value, entityExists);
        }
    }

    private void validateRange(Number value, ValidRange validRange) {
        int intValue = value.intValue();
        if (intValue < validRange.min() || intValue > validRange.max()) {
            throw new ValidationException(
                    String.format("%s: Value %d must be between %d and %d",
                            validRange.message(), intValue, validRange.min(), validRange.max())
            );
        }
    }

    private void validateLength(Object value, ValidLength validLength) {
        if (value == null) {
            if (!validLength.nullable()) {
                throw new ValidationException(validLength.message() + ": Value cannot be null");
            }
            return;
        }

        if (value instanceof String str) {
            if (str.length() < validLength.min() || str.length() > validLength.max()) {
                throw new ValidationException(
                        String.format("%s: Length %d must be between %d and %d",
                                validLength.message(), str.length(), validLength.min(), validLength.max())
                );
            }
        }
    }

    private void validateEntityExists(Object id, EntityExists entityExists) {
        Object entity = entityManager.find(entityExists.entity(), id);
        if (entity == null) {
            throw new EntityNotFoundException(
                    String.format("%s: %s with id %s not found",
                            entityExists.message(), entityExists.entity().getSimpleName(), id)
            );
        }
    }
}
