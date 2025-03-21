package com.app.foodcart.DTOs.requests;

import lombok.Data;

@Data
public class CartItemRequestDTO {
    private Long foodItemId;
    private int quantity;
}