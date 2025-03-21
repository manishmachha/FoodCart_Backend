package com.app.foodcart.DTOs.requests;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {
    private Long restaurantId;
    private List<OrderItemRequestDTO> orderItems;
}