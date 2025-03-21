package com.app.foodcart.DTOs.requests;

import lombok.Data;

@Data
public class OrderItemRequestDTO {
    private Long foodItemId;
    private int quantity;
}