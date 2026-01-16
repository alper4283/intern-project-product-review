package com.alper.product_review_backend.controller;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.domain.Role;
import com.alper.product_review_backend.domain.User;
import com.alper.product_review_backend.repository.ProductRepository;
import com.alper.product_review_backend.repository.ReviewRepository;
import com.alper.product_review_backend.repository.UserRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    private Long productId;
    private User testUser;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User("testuser", "test@example.com", "password123", Role.USER);
        testUser = userRepository.save(testUser);

        Product product = new Product(
                "Integration Test Product",
                "Description",
                "TestCategory",
                new BigDecimal("200.00")
        );
        product = productRepository.save(product);
        productId = product.getId();
    }

    @Test
    void postReview_createsReview_andUpdatesProductAggregates() throws Exception {
        String jsonBody = """
                {
                  "rating": 5,
                  "comment": "Integration test review"
                }
                """;

        // POST review with authenticated user
        mockMvc.perform(post("/api/products/{id}/reviews", productId)
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating", is(5)))
                .andExpect(jsonPath("$.comment", is("Integration test review")));

        // GET product and verify aggregates
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewCount", is(1)))
                .andExpect(jsonPath("$.averageRating", is(5.0)));
    }

    @Test
    void postReview_withInvalidRating_returnsBadRequest() throws Exception {
        String jsonBody = """
                {
                  "rating": 10,
                  "comment": "Too high rating"
                }
                """;

        mockMvc.perform(post("/api/products/{id}/reviews", productId)
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("BAD_REQUEST")));
    }

    @Test
    void postReview_withoutAuthentication_returnsUnauthorized() throws Exception {
        String jsonBody = """
                {
                  "rating": 5,
                  "comment": "Test review"
                }
                """;

        mockMvc.perform(post("/api/products/{id}/reviews", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postReview_withRatingTooLow_returnsBadRequest() throws Exception {
        String jsonBody = """
                {
                  "rating": 0,
                  "comment": "Rating too low"
                }
                """;

        mockMvc.perform(post("/api/products/{id}/reviews", productId)
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReviews_returnsEmptyListForNewProduct() throws Exception {
        mockMvc.perform(get("/api/products/{id}/reviews", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getReviews_returnsReviewsAfterPosting() throws Exception {
        // Post a review first
        String jsonBody = """
                {
                  "rating": 4,
                  "comment": "Good product"
                }
                """;

        mockMvc.perform(post("/api/products/{id}/reviews", productId)
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        // Get reviews
        mockMvc.perform(get("/api/products/{id}/reviews", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rating", is(4)))
                .andExpect(jsonPath("$[0].comment", is("Good product")))
                .andExpect(jsonPath("$[0].username", is("testuser")));
    }

    @Test
    void getReviews_isPubliclyAccessibleWithoutAuth() throws Exception {
        // No authentication - should still work
        mockMvc.perform(get("/api/products/{id}/reviews", productId))
                .andExpect(status().isOk());
    }

    @Test
    void postReview_forNonExistentProduct_returns404() throws Exception {
        String jsonBody = """
                {
                  "rating": 5,
                  "comment": "Test review"
                }
                """;

        mockMvc.perform(post("/api/products/{id}/reviews", 99999L)
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void multipleReviews_updateAverageCorrectly() throws Exception {
        // Post first review (5 stars)
        mockMvc.perform(post("/api/products/{id}/reviews", productId)
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\": 5, \"comment\": \"Excellent!\"}"))
                .andExpect(status().isOk());

        // Post second review (3 stars)
        mockMvc.perform(post("/api/products/{id}/reviews", productId)
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\": 3, \"comment\": \"Average\"}"))
                .andExpect(status().isOk());

        // Check product average: (5 + 3) / 2 = 4.0
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewCount", is(2)))
                .andExpect(jsonPath("$.averageRating", is(4.0)));
    }

    @Test
    void reviewResponse_includesUsername() throws Exception {
        String jsonBody = """
                {
                  "rating": 5,
                  "comment": "Test review"
                }
                """;

        mockMvc.perform(post("/api/products/{id}/reviews", productId)
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")));
    }
}
