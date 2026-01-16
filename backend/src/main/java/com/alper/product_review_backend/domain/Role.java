package com.alper.product_review_backend.domain;

/**
 * Enum representing user roles for Role-Based Access Control (RBAC).
 */
public enum Role {
    /**
     * Regular user - can view products and create reviews
     */
    USER,
    
    /**
     * Administrator - full access to all operations including user management
     */
    ADMIN
}
