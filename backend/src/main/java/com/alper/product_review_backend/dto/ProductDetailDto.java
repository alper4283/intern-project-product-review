package com.alper.product_review_backend.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto {

    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private double averageRating;
    private long reviewCount;
}

