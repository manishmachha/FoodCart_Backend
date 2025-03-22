package com.app.foodcart.DTOs.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RatingRequestDTO {
    private Long restaurantId;
    private Long foodItemId;

    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    private int stars;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;
}