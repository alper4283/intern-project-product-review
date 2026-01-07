package com.alper.product_review_backend.controller;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.repository.ProductRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private Long productId;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

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

        // POST review
        mockMvc.perform(post("/api/products/{id}/reviews", productId)
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("BAD_REQUEST")));
    }
}
