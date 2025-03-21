package com.app.foodcart.DTOs.requests;

import lombok.Data;

@Data
public class RatingRequestDTO {
    private Long restaurantId;
    private Long foodItemId;
    private int stars;
    private String comment;
}