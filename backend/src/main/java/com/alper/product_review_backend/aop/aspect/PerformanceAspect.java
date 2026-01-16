package com.alper.product_review_backend.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Aspect for monitoring service layer performance.
 * Logs slow method executions and tracks service metrics.
 */
@Slf4j
@Aspect
@Component
@Order(3)
public class PerformanceAspect {

    private static final long SLOW_THRESHOLD_MS = 500;

    /**
     * Monitor all service method executions for performance.
     */
    @Around("execution(* com.alper.product_review_backend.service..*.*(..))")
    public Object monitorServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // Log warning for slow methods
            if (executionTime > SLOW_THRESHOLD_MS) {
                log.warn("SLOW SERVICE CALL: {}.{} took {}ms (threshold: {}ms)", 
                        className, methodName, executionTime, SLOW_THRESHOLD_MS);
            } else if (log.isDebugEnabled()) {
                log.debug("Service call {}.{} took {}ms", className, methodName, executionTime);
            }

            return result;
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Service call {}.{} failed after {}ms: {}", 
                    className, methodName, executionTime, ex.getMessage());
            throw ex;
        }
    }

    /**
     * Monitor repository method executions for database performance.
     */
    @Around("execution(* com.alper.product_review_backend.repository..*.*(..))")
    public Object monitorRepositoryPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // Log warning for slow database queries
            if (executionTime > SLOW_THRESHOLD_MS / 2) { // Stricter threshold for DB
                log.warn("SLOW DB QUERY: {}.{} took {}ms", 
                        className, methodName, executionTime);
            }

            return result;
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("DB query {}.{} failed after {}ms: {}", 
                    className, methodName, executionTime, ex.getMessage());
            throw ex;
        }
    }
}
