package com.alper.product_review_backend.config;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return; // don't insert duplicates on dev restarts within same run
        }

        List<Product> products = List.of(
                new Product("iPhone 15 Pro", "Flagship smartphone", "Phones", new BigDecimal("1499.00")),
                new Product("Galaxy S24", "Android flagship", "Phones", new BigDecimal("1299.00")),
                new Product("Sony WH-1000XM5", "Noise-cancelling headphones", "Audio", new BigDecimal("399.00"))
        );

        productRepository.saveAll(products);
    }
}
