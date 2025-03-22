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

        // Safely access Order properties
        if (orderItem.getOrder() != null) {
            try {
                this.orderId = orderItem.getOrder().getId();
            } catch (Exception e) {
                // If lazy loading exception occurs, just continue
                System.err.println(
                        "Error loading order data for order item " + orderItem.getId() + ": " + e.getMessage());
            }
        }

        // Safely access FoodItem properties
        if (orderItem.getFoodItem() != null) {
            try {
                this.foodItemId = orderItem.getFoodItem().getId();
                this.foodItemName = orderItem.getFoodItem().getName();
                this.foodItemPrice = orderItem.getFoodItem().getPrice().doubleValue();
            } catch (Exception e) {
                // If lazy loading exception occurs, just continue
                System.err.println(
                        "Error loading food item data for order item " + orderItem.getId() + ": " + e.getMessage());
                // Try to set ID at least
                this.foodItemId = orderItem.getFoodItem().getId(); // ID should be available
            }
        }

        this.quantity = orderItem.getQuantity();
    }
}