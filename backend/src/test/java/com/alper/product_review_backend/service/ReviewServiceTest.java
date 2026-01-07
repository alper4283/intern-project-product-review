package com.alper.product_review_backend.service;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.domain.Review;
import com.alper.product_review_backend.repository.ProductRepository;
import com.alper.product_review_backend.repository.ReviewRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        productRepository.deleteAll();

        product = new Product(
                "Test Product",
                "For service tests",
                "TestCategory",
                new BigDecimal("100.00")
        );
        product = productRepository.save(product);
    }

    @Test
    void addReview_updatesAverageRatingAndCount() {
        // first review: rating 4
        Review r1 = reviewService.addReview(product.getId(), 4, "Good");
        assertThat(r1.getId()).isNotNull();

        Product afterFirst = productRepository.findById(product.getId()).orElseThrow();
        assertThat(afterFirst.getReviewCount()).isEqualTo(1);
        assertThat(afterFirst.getAverageRating()).isEqualTo(4.0);

        // second review: rating 2
        Review r2 = reviewService.addReview(product.getId(), 2, "Meh");
        assertThat(r2.getId()).isNotNull();

        Product afterSecond = productRepository.findById(product.getId()).orElseThrow();
        assertThat(afterSecond.getReviewCount()).isEqualTo(2);
        // (4 + 2) / 2 = 3.0
        assertThat(afterSecond.getAverageRating()).isEqualTo(3.0);
    }
}
