package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.OrderItem;

@Data
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long foodItemId;
    private String foodItemName;
    private double foodItemPrice;
    private int quantity;

    public OrderItemDTO(OrderItem orderItem) {
        this.id = orderItem.getId();

        if (orderItem.getOrder() != null) {
            this.orderId = orderItem.getOrder().getId();
        }

        if (orderItem.getFoodItem() != null) {
            this.foodItemId = orderItem.getFoodItem().getId();
            this.foodItemName = orderItem.getFoodItem().getName();
            this.foodItemPrice = orderItem.getFoodItem().getPrice().doubleValue();
        }

        this.quantity = orderItem.getQuantity();
    }
}