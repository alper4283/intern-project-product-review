package com.alper.product_review_backend.config;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.domain.Role;
import com.alper.product_review_backend.domain.User;
import com.alper.product_review_backend.repository.ProductRepository;
import com.alper.product_review_backend.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeUsers();
        initializeProducts();
    }

    private void initializeUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        // Create default admin user for development
        User admin = new User(
                "admin",
                "admin@example.com",
                passwordEncoder.encode("admin123"),
                Role.ADMIN
        );
        userRepository.save(admin);
        log.info("Created default admin user: admin / admin123");

        // Create default test user
        User user = new User(
                "user",
                "user@example.com",
                passwordEncoder.encode("user123"),
                Role.USER
        );
        userRepository.save(user);
        log.info("Created default test user: user / user123");
    }

    private void initializeProducts() {
        if (productRepository.count() > 0) {
            return; // don't insert duplicates on dev restarts within same run
        }

        List<Product> products = List.of(
                new Product("iPhone 15 Pro", "Flagship smartphone with titanium design", "Phones", new BigDecimal("1499.00")),
                new Product("Galaxy S24 Ultra", "Android flagship with S Pen", "Phones", new BigDecimal("1299.00")),
                new Product("Google Pixel 8 Pro", "Pure Android experience with AI features", "Phones", new BigDecimal("999.00")),
                new Product("OnePlus 12", "Fast charging Android flagship", "Phones", new BigDecimal("899.00")),
                new Product("iPhone 14", "Previous-gen iPhone with great performance", "Phones", new BigDecimal("799.00")),
                new Product("Samsung Galaxy A54", "Mid-range Android phone", "Phones", new BigDecimal("449.00")),
                new Product("Xiaomi 14", "Compact high-performance phone", "Phones", new BigDecimal("699.00")),
                new Product("Sony Xperia 1 V", "Phone with professional camera features", "Phones", new BigDecimal("1399.00")),

                new Product("MacBook Pro 14 M3", "Powerful laptop for professionals", "Laptops", new BigDecimal("1999.00")),
                new Product("MacBook Air M2", "Lightweight and efficient laptop", "Laptops", new BigDecimal("1199.00")),
                new Product("Dell XPS 13", "Premium ultrabook with thin bezels", "Laptops", new BigDecimal("1299.00")),
                new Product("Lenovo ThinkPad X1 Carbon", "Business laptop with great keyboard", "Laptops", new BigDecimal("1499.00")),
                new Product("ASUS Zenbook 14 OLED", "OLED display ultrabook", "Laptops", new BigDecimal("1099.00")),
                new Product("HP Spectre x360", "2-in-1 convertible laptop", "Laptops", new BigDecimal("1249.00")),
                new Product("Microsoft Surface Laptop 5", "Premium Windows laptop", "Laptops", new BigDecimal("1299.00")),

                new Product("iPad Pro 12.9", "Professional tablet with M2 chip", "Tablets", new BigDecimal("1099.00")),
                new Product("iPad Air", "Versatile mid-range tablet", "Tablets", new BigDecimal("599.00")),
                new Product("Samsung Galaxy Tab S9", "High-end Android tablet", "Tablets", new BigDecimal("799.00")),
                new Product("Microsoft Surface Pro 9", "2-in-1 Windows tablet", "Tablets", new BigDecimal("999.00")),
                new Product("Lenovo Tab P11 Pro", "Productivity Android tablet", "Tablets", new BigDecimal("499.00")),

                new Product("Sony WH-1000XM5", "Industry-leading noise-cancelling headphones", "Audio", new BigDecimal("399.00")),
                new Product("AirPods Pro 2", "Wireless earbuds with active noise cancellation", "Audio", new BigDecimal("249.00")),
                new Product("Bose QuietComfort 45", "Comfortable noise-cancelling headphones", "Audio", new BigDecimal("329.00")),
                new Product("Sennheiser Momentum 4", "Long battery life wireless headphones", "Audio", new BigDecimal("349.00")),
                new Product("Anker Soundcore Life Q30", "Budget noise-cancelling headphones", "Audio", new BigDecimal("79.00")),
                new Product("Jabra Elite 7 Pro", "Premium true wireless earbuds", "Audio", new BigDecimal("199.00")),

                new Product("Apple Watch Series 9", "Latest Apple smartwatch", "Wearables", new BigDecimal("399.00")),
                new Product("Samsung Galaxy Watch 6", "Android-compatible smartwatch", "Wearables", new BigDecimal("349.00")),
                new Product("Garmin Forerunner 265", "Running and fitness watch", "Wearables", new BigDecimal("449.00")),
                new Product("Fitbit Versa 4", "Fitness-focused smartwatch", "Wearables", new BigDecimal("229.00")),

                new Product("Logitech MX Keys", "Premium wireless keyboard", "Accessories", new BigDecimal("119.00")),
                new Product("Anker 65W GaN Charger", "Compact fast charger", "Accessories", new BigDecimal("49.00"))
        );

        productRepository.saveAll(products);
        log.info("Initialized {} products", products.size());
    }
}