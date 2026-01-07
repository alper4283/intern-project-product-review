package com.alper.product_review_backend.controller;

import com.alper.product_review_backend.domain.Product;
import com.alper.product_review_backend.dto.ProductDetailDto;
import com.alper.product_review_backend.dto.ProductSummaryDto;
import com.alper.product_review_backend.service.ProductService;
import com.alper.product_review_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService; 

    /**
     * GET /api/products?page=0&size=10&sort=price,asc
     * Returns a page of product summaries.
     */
    @GetMapping
    public Page<ProductSummaryDto> getProducts(Pageable pageable) {
        Page<Product> page = productService.getProducts(pageable);
        return page.map(this::toProductSummaryDto);
    }

    /**
     * GET /api/products/{id}
     * Returns detailed information for a single product.
     */
    @GetMapping("/{id}")
    public ProductDetailDto getProductById(@PathVariable Long id) {
        Product product = productService.getProductOrThrow(id);
        return toProductDetailDto(product);
    }

    
    private ProductSummaryDto toProductSummaryDto(Product product) {
        return new ProductSummaryDto(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getAverageRating(),
                product.getReviewCount()
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
