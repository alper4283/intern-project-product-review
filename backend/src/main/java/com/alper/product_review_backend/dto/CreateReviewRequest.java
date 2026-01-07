package com.alper.product_review_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReviewRequest {

    @Min(1)
    @Max(5)
    private int rating;

    // Optional comment, but limit length if present
    @Size(max = 2000)
    private String comment;
}

