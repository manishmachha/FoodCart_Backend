package com.app.foodcart.DTOs.requests;

import lombok.Data;
import java.util.List;

@Data
public class AddToCartRequestDTO {
    private Long restaurantId;
    private List<CartItemRequestDTO> items;
}