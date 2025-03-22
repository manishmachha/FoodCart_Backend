package com.app.foodcart.DTOs.requests;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class OrderItemRequestDTO {
    @NotNull(message = "Food item ID is required")
    private Long foodItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}