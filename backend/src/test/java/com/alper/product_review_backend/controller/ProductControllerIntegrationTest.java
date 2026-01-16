package com.alper.product_review_backend.controller;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.repository.ProductRepository;
import com.alper.product_review_backend.repository.ReviewRepository;
import com.alper.product_review_backend.repository.UserRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Product API endpoints.
 * Tests product listing, pagination, sorting, and detail view.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Create test products with different categories and prices
        productRepository.save(new Product("iPhone 15", "Latest Apple phone", "Electronics", new BigDecimal("999.99")));
        productRepository.save(new Product("Samsung Galaxy", "Android flagship", "Electronics", new BigDecimal("899.99")));
        productRepository.save(new Product("MacBook Pro", "Apple laptop", "Electronics", new BigDecimal("2499.99")));
        productRepository.save(new Product("Java Programming", "Learn Java", "Books", new BigDecimal("49.99")));
        productRepository.save(new Product("Spring Boot Guide", "Master Spring", "Books", new BigDecimal("39.99")));
    }

    @Test
    @DisplayName("GET /api/products - Should return paginated list of products")
    void getProducts_returnsPaginatedProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements", is(5)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.content[0].id", notNullValue()))
                .andExpect(jsonPath("$.content[0].name", notNullValue()))
                .andExpect(jsonPath("$.content[0].category", notNullValue()))
                .andExpect(jsonPath("$.content[0].price", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/products - Should support pagination with size parameter")
    void getProducts_supportsPagination() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(5)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(false)));
    }

    @Test
    @DisplayName("GET /api/products - Should support sorting by price ascending")
    void getProducts_supportsSortingByPriceAsc() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("sort", "price,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].price", is(39.99)))
                .andExpect(jsonPath("$.content[1].price", is(49.99)))
                .andExpect(jsonPath("$.content[4].price", is(2499.99)));
    }

    @Test
    @DisplayName("GET /api/products - Should support sorting by price descending")
    void getProducts_supportsSortingByPriceDesc() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("sort", "price,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].price", is(2499.99)))
                .andExpect(jsonPath("$.content[4].price", is(39.99)));
    }

    @Test
    @DisplayName("GET /api/products - Should support sorting by name")
    void getProducts_supportsSortingByName() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("sort", "name,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("Java Programming")));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product details")
    void getProductById_returnsProductDetails() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(get("/api/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.description", is(product.getDescription())))
                .andExpect(jsonPath("$.category", is(product.getCategory())))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.averageRating", is(0.0)))
                .andExpect(jsonPath("$.reviewCount", is(0)));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 for non-existent product")
    void getProductById_returns404ForNonExistent() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("GET /api/products - Second page should have remaining products")
    void getProducts_secondPageHasRemainingProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.first", is(false)))
                .andExpect(jsonPath("$.last", is(true)));
    }

    @Test
    @DisplayName("GET /api/products - Empty page should return empty content")
    void getProducts_emptyPageReturnsEmptyContent() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "100")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.empty", is(true)));
    }

    @Test
    @DisplayName("Products endpoint should be publicly accessible without authentication")
    void getProducts_isPubliclyAccessible() throws Exception {
        // No authentication header provided - should still work
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }
}
