package com.alper.product_review_backend.service;

import com.alper.product_review_backend.domain.Role;
import com.alper.product_review_backend.domain.User;
import com.alper.product_review_backend.dto.auth.AuthResponse;
import com.alper.product_review_backend.dto.auth.LoginRequest;
import com.alper.product_review_backend.dto.auth.RegisterRequest;
import com.alper.product_review_backend.exception.UserAlreadyExistsException;
import com.alper.product_review_backend.repository.UserRepository;
import com.alper.product_review_backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication service handling user registration and login.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create new user
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER
        );

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user);

        return AuthResponse.of(
                token,
                jwtTokenProvider.getExpirationTime(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    /**
     * Authenticate user and return JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(authentication);

        log.info("User logged in successfully: {}", user.getUsername());

        return AuthResponse.of(
                token,
                jwtTokenProvider.getExpirationTime(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    /**
     * Register a new admin user (admin-only operation).
     */
    @Transactional
    public AuthResponse registerAdmin(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create new admin user
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.ADMIN
        );

        userRepository.save(user);
        log.info("Admin user registered successfully: {}", user.getUsername());

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user);

        return AuthResponse.of(
                token,
                jwtTokenProvider.getExpirationTime(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
