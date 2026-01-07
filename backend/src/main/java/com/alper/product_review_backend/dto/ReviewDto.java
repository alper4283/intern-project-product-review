package com.alper.product_review_backend.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private Long id;
    private int rating;
    private String comment;
    private Instant createdAt;
}
