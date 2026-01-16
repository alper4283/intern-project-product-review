package com.alper.product_review_backend.controller;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.domain.User;
import com.alper.product_review_backend.dto.CreateProductRequest;
import com.alper.product_review_backend.dto.ProductDetailDto;
import com.alper.product_review_backend.dto.UserDto;
import com.alper.product_review_backend.dto.auth.AuthResponse;
import com.alper.product_review_backend.dto.auth.RegisterRequest;
import com.alper.product_review_backend.repository.UserRepository;
import com.alper.product_review_backend.service.AuthService;
import com.alper.product_review_backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for administrative operations.
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ProductService productService;

    /**
     * POST /api/admin/users
     * Create a new admin user (admin-only).
     */
    @PostMapping("/users")
    public ResponseEntity<AuthResponse> createAdmin(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/admin/users
     * Get all users (admin-only).
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = users.stream()
                .map(this::toUserDto)
                .toList();
        return ResponseEntity.ok(userDtos);
    }

    /**
     * POST /api/admin/products
     * Create a new product (admin-only).
     */
    @PostMapping("/products")
    public ResponseEntity<ProductDetailDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                request.getPrice()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toProductDetailDto(product));
    }

    private UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.isEnabled()
        );
    }

    private ProductDetailDto toProductDetailDto(Product product) {
        return new ProductDetailDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getAverageRating(),
                product.getReviewCount()
        );
    }
}
