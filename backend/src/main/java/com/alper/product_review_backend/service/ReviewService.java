package com.alper.product_review_backend.service;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.domain.Review;
import com.alper.product_review_backend.repository.ProductRepository;
import com.alper.product_review_backend.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    // Fetch reviews for a specific product, ordered by creation date descending
    public List<Review> getReviewsForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        return reviewRepository.findByProductOrderByCreatedAtDesc(product);
    }

    @Transactional
    public Review addReview(Long productId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

        // 1) Save the new review
        Review review = new Review(product, rating, comment);
        reviewRepository.save(review);

        // 2) Update aggregate fields on Product
        long oldCount = product.getReviewCount();
        double oldAverage = product.getAverageRating();

        long newCount = oldCount + 1;
        double newAverage = ((oldAverage * oldCount) + rating) / newCount;

        product.setReviewCount(newCount);
        product.setAverageRating(newAverage);

        productRepository.save(product);

        return review;
    }
}
