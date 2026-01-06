package com.alper.product_review_backend.repository;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.domain.Review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductOrderByCreatedAtDesc(Product product);
}
