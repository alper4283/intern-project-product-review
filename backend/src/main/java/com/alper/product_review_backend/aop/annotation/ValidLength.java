package com.alper.product_review_backend.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to validate string length constraints.
 * Can be applied to method parameters for automatic validation.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLength {
    
    /**
     * Minimum length (inclusive).
     */
    int min() default 0;
    
    /**
     * Maximum length (inclusive).
     */
    int max() default Integer.MAX_VALUE;
    
    /**
     * Whether to allow null values.
     */
    boolean nullable() default true;
    
    /**
     * Custom error message.
     */
    String message() default "String length must be within the specified range";
}
