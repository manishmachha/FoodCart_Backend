package com.app.foodcart.DTOs.requests;

import lombok.Data;
import com.app.foodcart.entities.enums.OrderStatus;
import java.util.List;

@Data
public class UpdateOrderRequestDTO {
    private OrderStatus status;
    private Long deliveryAddressId;
    private List<OrderItemRequestDTO> orderItems;
}