package com.alper.product_review_backend.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to validate that a numeric value is within a specified range.
 * Can be applied to method parameters for automatic validation.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRange {
    
    /**
     * Minimum allowed value (inclusive).
     */
    int min() default Integer.MIN_VALUE;
    
    /**
     * Maximum allowed value (inclusive).
     */
    int max() default Integer.MAX_VALUE;
    
    /**
     * Custom error message.
     */
    String message() default "Value must be within the specified range";
}
