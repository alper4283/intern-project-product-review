package com.alper.product_review_backend.aop.aspect;

import com.alper.product_review_backend.aop.annotation.LogExecution;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for logging method execution with timing information.
 * Useful for debugging and performance monitoring.
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class LoggingAspect {

    /**
     * Log method execution for methods annotated with @LogExecution.
     */
    @Around("@annotation(logExecution)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint, LogExecution logExecution) throws Throwable {
        return executeWithLogging(joinPoint, logExecution.logParams(), logExecution.logResult());
    }

    /**
     * Log method execution for all methods in classes annotated with @LogExecution.
     */
    @Around("@within(logExecution)")
    public Object logClassMethodExecution(ProceedingJoinPoint joinPoint, LogExecution logExecution) throws Throwable {
        return executeWithLogging(joinPoint, logExecution.logParams(), logExecution.logResult());
    }

    /**
     * Log all controller method executions for debugging.
     */
    @Around("execution(* com.alper.product_review_backend.controller..*.*(..))")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        return executeWithLogging(joinPoint, true, false);
    }

    private Object executeWithLogging(ProceedingJoinPoint joinPoint, boolean logParams, boolean logResult) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        // Log method entry
        if (logParams) {
            log.info(">>> {}.{} called with args: {}", 
                    className, methodName, Arrays.toString(joinPoint.getArgs()));
        } else {
            log.info(">>> {}.{} called", className, methodName);
        }

        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // Log method exit
            if (logResult) {
                log.info("<<< {}.{} completed in {}ms with result: {}", 
                        className, methodName, executionTime, result);
            } else {
                log.info("<<< {}.{} completed in {}ms", className, methodName, executionTime);
            }

            return result;
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("<<< {}.{} failed after {}ms with exception: {}", 
                    className, methodName, executionTime, ex.getMessage());
            throw ex;
        }
    }
}
