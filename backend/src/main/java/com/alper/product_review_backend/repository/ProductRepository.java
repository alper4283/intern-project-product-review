package com.alper.product_review_backend.repository;

import com.alper.product_review_backend.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> { 
    
}
