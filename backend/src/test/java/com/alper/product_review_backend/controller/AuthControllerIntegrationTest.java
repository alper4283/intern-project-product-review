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
 * Integration tests for Authentication API endpoints.
 * Tests user registration, login, and JWT token functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String uniqueId;

    @BeforeEach
    void setUp() {
        uniqueId = UUID.randomUUID().toString().substring(0, 8);
    }

    // ==================== REGISTRATION TESTS ====================

    @Test
    @DisplayName("POST /api/auth/register - Should register new user successfully")
    void register_success() throws Exception {
        String requestBody = String.format("""
                {
                  "username": "newuser_%s",
                  "email": "newuser_%s@example.com",
                  "password": "password123"
                }
                """, uniqueId, uniqueId);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.username", is("newuser_" + uniqueId)))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.expiresIn", greaterThan(0)));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 409 for duplicate username")
    void register_duplicateUsername_returns409() throws Exception {
        // Create existing user with unique email
        String existingUsername = "existinguser_" + uniqueId;
        String existingEmail = "existing_" + uniqueId + "@example.com";
        userRepository.save(new User(existingUsername, existingEmail, 
                passwordEncoder.encode("password"), Role.USER));

        String requestBody = String.format("""
                {
                  "username": "%s",
                  "email": "new_%s@example.com",
                  "password": "password123"
                }
                """, existingUsername, uniqueId);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("CONFLICT")))
                .andExpect(jsonPath("$.message", containsString("Username already exists")));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 409 for duplicate email")
    void register_duplicateEmail_returns409() throws Exception {
        // Create existing user with unique email
        String existingUsername = "existinguser_" + uniqueId;
        String existingEmail = "existing_" + uniqueId + "@example.com";
        userRepository.save(new User(existingUsername, existingEmail, 
                passwordEncoder.encode("password"), Role.USER));

        String requestBody = String.format("""
                {
                  "username": "newuser_%s",
                  "email": "%s",
                  "password": "password123"
                }
                """, uniqueId, existingEmail);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("CONFLICT")))
                .andExpect(jsonPath("$.message", containsString("Email already exists")));
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 for empty username")
    void register_emptyUsername_returns400() throws Exception {
        String requestBody = """
                {
                  "username": "",
                  "email": "test@example.com",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 for invalid email format")
    void register_invalidEmail_returns400() throws Exception {
        String requestBody = """
                {
                  "username": "testuser",
                  "email": "not-an-email",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should return 400 for short password")
    void register_shortPassword_returns400() throws Exception {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String requestBody = String.format("""
                {
                  "username": "shortpw_%s",
                  "email": "shortpw_%s@example.com",
                  "password": "123"
                }
                """, uniqueId, uniqueId);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("POST /api/auth/login - Should login successfully with valid credentials")
    void login_success() throws Exception {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String username = "loginuser_" + uniqueId;
        // Create user first
        userRepository.save(new User(username, "login_" + uniqueId + "@example.com", 
                passwordEncoder.encode("password123"), Role.USER));

        String requestBody = String.format("""
                {
                  "username": "%s",
                  "password": "password123"
                }
                """, username);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 401 for wrong password")
    void login_wrongPassword_returns401() throws Exception {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String username = "wrongpw_" + uniqueId;
        // Create user first
        userRepository.save(new User(username, "wrongpw_" + uniqueId + "@example.com", 
                passwordEncoder.encode("password123"), Role.USER));

        String requestBody = String.format("""
                {
                  "username": "%s",
                  "password": "wrongpassword"
                }
                """, username);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - Should return 401 for non-existent user")
    void login_nonExistentUser_returns401() throws Exception {
        String requestBody = """
                {
                  "username": "nonexistent",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    // ==================== JWT TOKEN TESTS ====================

    @Test
    @DisplayName("JWT token should allow access to protected endpoints")
    void jwtToken_allowsAccessToProtectedEndpoints() throws Exception {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        // Register and get token
        String registerBody = String.format("""
                {
                  "username": "jwtuser_%s",
                  "email": "jwt_%s@example.com",
                  "password": "password123"
                }
                """, uniqueId, uniqueId);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract token from response
        String response = result.getResponse().getContentAsString();
        String token = extractTokenFromResponse(response);

        // Use token to access a protected endpoint (posting a review requires auth)
        // Just verify the token is valid by accessing any endpoint that would otherwise fail
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Invalid JWT token should be rejected on protected endpoint")
    void invalidJwtToken_isRejected() throws Exception {
        // Use a protected endpoint (admin endpoint requires authentication)
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Malformed Authorization header should be rejected")
    void malformedAuthHeader_isRejected() throws Exception {
        mockMvc.perform(post("/api/products/1/reviews")
                        .header("Authorization", "NotBearer sometoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\": 5, \"comment\": \"test\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Admin user should have ADMIN role in token")
    void adminUser_hasAdminRole() throws Exception {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String adminUsername = "admintest_" + uniqueId;
        // Create admin user
        userRepository.save(new User(adminUsername, "admintest_" + uniqueId + "@example.com", 
                passwordEncoder.encode("admin123"), Role.ADMIN));

        String loginBody = String.format("""
                {
                  "username": "%s",
                  "password": "admin123"
                }
                """, adminUsername);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("ADMIN")));
    }

    // Helper method to extract token from JSON response
    private String extractTokenFromResponse(String response) {
        // Simple extraction - in real tests you might use Jackson
        int start = response.indexOf("\"token\":\"") + 9;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }
}
