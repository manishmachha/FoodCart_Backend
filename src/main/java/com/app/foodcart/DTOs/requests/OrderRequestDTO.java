package com.app.foodcart.DTOs.requests;

import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;

@Data
public class OrderRequestDTO {
    private Long deliveryAddressId; // Optional, will use first address if not provided

    @NotEmpty(message = "Order must contain at least one item")
    @Valid // Ensures validation is applied to each item in the list
    private List<OrderItemRequestDTO> orderItems;
}