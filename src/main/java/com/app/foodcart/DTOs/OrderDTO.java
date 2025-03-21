package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.Order;
import com.app.foodcart.entities.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long restaurantId;
    private String restaurantName;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime orderTime;
    private OrderStatus status;

    public OrderDTO(Order order) {
        this.id = order.getId();

        if (order.getUser() != null) {
            this.userId = order.getUser().getId();
            this.userName = order.getUser().getName();
        }

        if (order.getRestaurant() != null) {
            this.restaurantId = order.getRestaurant().getId();
            this.restaurantName = order.getRestaurant().getName();
        }

        if (order.getOrderItems() != null) {
            this.orderItems = order.getOrderItems().stream()
                    .map(OrderItemDTO::new)
                    .collect(Collectors.toList());
        }

        this.orderTime = order.getOrderTime();
        this.status = order.getStatus();
    }
}