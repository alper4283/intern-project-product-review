package com.alper.product_review_backend.controller;

import com.alper.product_review_backend.domain.Role;
import com.alper.product_review_backend.domain.User;
import com.alper.product_review_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Admin API endpoints.
 * Tests RBAC (Role-Based Access Control) and admin operations.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;
    private String uniqueId;

    @BeforeEach
    void setUp() throws Exception {
        uniqueId = UUID.randomUUID().toString().substring(0, 8);
        
        // Create and login admin user with unique email
        User admin = new User("admin_" + uniqueId, "admin_" + uniqueId + "@example.com", 
                passwordEncoder.encode("admin123"), Role.ADMIN);
        userRepository.save(admin);
        adminToken = loginAndGetToken("admin_" + uniqueId, "admin123");

        // Create and login regular user with unique email
        User regularUser = new User("user_" + uniqueId, "user_" + uniqueId + "@example.com", 
                passwordEncoder.encode("user123"), Role.USER);
        userRepository.save(regularUser);
        userToken = loginAndGetToken("user_" + uniqueId, "user123");
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String loginBody = String.format("""
                {
                  "username": "%s",
                  "password": "%s"
                }
                """, username, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int start = response.indexOf("\"token\":\"") + 9;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }

    // ==================== ADMIN USER CREATION TESTS ====================

    @Test
    @DisplayName("POST /api/admin/users - Admin can create new admin user")
    void createAdmin_asAdmin_success() throws Exception {
        String requestBody = """
                {
                  "username": "newadmin",
                  "email": "newadmin@example.com",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("newadmin")))
                .andExpect(jsonPath("$.role", is("ADMIN")))
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/admin/users - Regular user cannot create admin (403 Forbidden)")
    void createAdmin_asUser_returns403() throws Exception {
        String requestBody = """
                {
                  "username": "newadmin",
                  "email": "newadmin@example.com",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.error", is("FORBIDDEN")));
    }

    @Test
    @DisplayName("POST /api/admin/users - Unauthenticated request returns 401")
    void createAdmin_unauthenticated_returns401() throws Exception {
        String requestBody = """
                {
                  "username": "newadmin",
                  "email": "newadmin@example.com",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    // ==================== GET USERS TESTS ====================

    @Test
    @DisplayName("GET /api/admin/users - Admin can list all users")
    void getUsers_asAdmin_success() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].email", notNullValue()))
                .andExpect(jsonPath("$[0].role", notNullValue()))
                // Password should NOT be included in response
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/admin/users - Regular user cannot list users (403 Forbidden)")
    void getUsers_asUser_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/users - Unauthenticated request returns 401")
    void getUsers_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== CREATE PRODUCT TESTS ====================

    @Test
    @DisplayName("POST /api/admin/products - Admin can create new product")
    void createProduct_asAdmin_success() throws Exception {
        String requestBody = """
                {
                  "name": "New Product",
                  "description": "Product description",
                  "category": "Electronics",
                  "price": 299.99
                }
                """;

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.description", is("Product description")))
                .andExpect(jsonPath("$.category", is("Electronics")))
                .andExpect(jsonPath("$.price", is(299.99)))
                .andExpect(jsonPath("$.averageRating", is(0.0)))
                .andExpect(jsonPath("$.reviewCount", is(0)));
    }

    @Test
    @DisplayName("POST /api/admin/products - Regular user cannot create product (403 Forbidden)")
    void createProduct_asUser_returns403() throws Exception {
        String requestBody = """
                {
                  "name": "New Product",
                  "description": "Product description",
                  "category": "Electronics",
                  "price": 299.99
                }
                """;

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/admin/products - Should validate required fields")
    void createProduct_missingName_returns400() throws Exception {
        String requestBody = """
                {
                  "description": "Product description",
                  "category": "Electronics",
                  "price": 299.99
                }
                """;

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/admin/products - Should validate price is positive")
    void createProduct_negativePrice_returns400() throws Exception {
        String requestBody = """
                {
                  "name": "New Product",
                  "description": "Product description",
                  "category": "Electronics",
                  "price": -10.00
                }
                """;

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // ==================== RBAC EDGE CASES ====================

    @Test
    @DisplayName("Expired or invalid token should return 401")
    void invalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Token without Bearer prefix should fail")
    void tokenWithoutBearer_returns401() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", adminToken))
                .andExpect(status().isUnauthorized());
    }
}
