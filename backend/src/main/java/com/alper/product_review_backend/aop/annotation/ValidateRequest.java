package com.alper.product_review_backend.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that require validation.
 * When applied to a method, the ValidationAspect will automatically
 * validate any @Valid annotated parameters and handle validation errors consistently.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateRequest {
    
    /**
     * Custom error message prefix.
     */
    String message() default "Validation failed";
}
