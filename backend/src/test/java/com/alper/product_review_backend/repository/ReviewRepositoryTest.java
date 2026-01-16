package com.alper.product_review_backend.repository;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.domain.Review;
import com.alper.product_review_backend.domain.Role;
import com.alper.product_review_backend.domain.User;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByProductOrderByCreatedAtDesc_returnsNewestFirst() {
        // Create test user
        User user = new User("testuser", "test@example.com", "password123", Role.USER);
        user = userRepository.save(user);

        Product product = new Product(
                "Repo Test Product",
                "Description",
                "TestCategory",
                new BigDecimal("50.00")
        );
        product = productRepository.save(product);

        Review oldReview = new Review(product, user, 3, "Old");
        oldReview.setCreatedAt(Instant.now().minus(1, ChronoUnit.DAYS));

        Review newReview = new Review(product, user, 5, "New");
        newReview.setCreatedAt(Instant.now());

        reviewRepository.save(oldReview);
        reviewRepository.save(newReview);

        List<Review> result = reviewRepository.findByProductOrderByCreatedAtDesc(product);

        assertThat(result).hasSize(2);
        // first element should be newest
        assertThat(result.get(0).getComment()).isEqualTo("New");
        assertThat(result.get(1).getComment()).isEqualTo("Old");
    }
}
