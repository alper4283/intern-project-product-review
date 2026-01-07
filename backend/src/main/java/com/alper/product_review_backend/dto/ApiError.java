package com.alper.product_review_backend.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private Instant timestamp;
    private int status;
    private String error;      // e.g. "NOT_FOUND", "BAD_REQUEST"
    private String message;
    private String path;
    private List<String> details; // optional extra info 
}
