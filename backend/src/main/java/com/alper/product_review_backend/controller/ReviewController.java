package com.alper.product_review_backend.controller;


import com.alper.product_review_backend.domain.Review;
import com.alper.product_review_backend.domain.User;
import com.alper.product_review_backend.dto.CreateReviewRequest;
import com.alper.product_review_backend.dto.ReviewDto;
import com.alper.product_review_backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;

    @GetMapping
    public List<ReviewDto> getReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsForProduct(productId);
        return reviews.stream()
                .map(this::toReviewDto)
                .toList();
    }

    @PostMapping
    public ReviewDto addReview(@PathVariable Long productId,
                               @Valid @RequestBody CreateReviewRequest request,
                               @AuthenticationPrincipal User currentUser) {

        Review review = reviewService.addReview(
                productId,
                currentUser,
                request.getRating(),
                request.getComment()
        );

        return toReviewDto(review);
    }


    private ReviewDto toReviewDto(Review review) {
        return new ReviewDto(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getUser().getUsername(),
                review.getCreatedAt()
        );
    }
}
