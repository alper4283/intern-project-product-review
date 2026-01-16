package com.alper.product_review_backend.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to log method execution with timing information.
 * Useful for debugging and performance monitoring.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecution {
    
    /**
     * Whether to log method parameters.
     */
    boolean logParams() default true;
    
    /**
     * Whether to log the return value.
     */
    boolean logResult() default false;
}
