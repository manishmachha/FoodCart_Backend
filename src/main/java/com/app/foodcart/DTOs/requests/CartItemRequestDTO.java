package com.app.foodcart.DTOs.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequestDTO {
    @NotNull(message = "Food item ID is required")
    private Long foodItemId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}