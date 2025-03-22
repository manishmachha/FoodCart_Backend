package com.app.foodcart.DTOs.requests;

import lombok.Data;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class AddToCartRequestDTO {
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "Items are required")
    @Size(min = 1, message = "At least one item is required")
    private List<CartItemRequestDTO> items;
}