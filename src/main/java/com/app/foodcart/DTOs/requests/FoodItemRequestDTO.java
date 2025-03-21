package com.app.foodcart.DTOs.requests;

import lombok.Data;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

@Data
public class FoodItemRequestDTO {
    private String name;
    @NotNull(message = "Price is required")
    private BigDecimal price;
    @NotNull(message = "Image is required")
    private String image;
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
    private boolean isActive;
}