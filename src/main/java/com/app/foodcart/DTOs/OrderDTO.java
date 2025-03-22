package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.Order;
import com.app.foodcart.entities.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private String userName;

    // Restaurant information
    private Long restaurantId;
    private String restaurantName;
    private String restaurantLocation;
    private String restaurantPhoneNumber;

    // Order items
    private List<OrderItemDTO> orderItems = new ArrayList<>();

    // Order details
    private LocalDateTime orderTime;
    private OrderStatus status;
    private int totalItems;
    private double totalAmount;

    public OrderDTO(Order order) {
        this.id = order.getId();

        // Safely access User properties
        if (order.getUser() != null) {
            try {
                this.userId = order.getUser().getId();
                this.userName = order.getUser().getName();
            } catch (Exception e) {
                // If lazy loading exception occurs, log it and continue
                System.err.println("Error loading user data for order " + order.getId() + ": " + e.getMessage());
                // Set minimal user data from the order if available
                this.userId = order.getUser().getId(); // ID should be available
            }
        }

        // Safely access Restaurant properties
        if (order.getRestaurant() != null) {
            try {
                this.restaurantId = order.getRestaurant().getId();
                this.restaurantName = order.getRestaurant().getName();
                this.restaurantLocation = order.getRestaurant().getLocation();
                this.restaurantPhoneNumber = order.getRestaurant().getPhoneNumber();
            } catch (Exception e) {
                // If lazy loading exception occurs, log it and continue
                System.err.println("Error loading restaurant data for order " + order.getId() + ": " + e.getMessage());
                // Set minimal restaurant data from the order if available
                this.restaurantId = order.getRestaurant().getId(); // ID should be available
            }
        }

        // Initialize with empty list
        this.orderItems = new ArrayList<>();

        // Safely process order items
        if (order.getOrderItems() != null) {
            try {
                this.orderItems = order.getOrderItems().stream()
                        .map(OrderItemDTO::new)
                        .collect(Collectors.toList());

                // Calculate totals
                this.totalItems = this.orderItems.stream()
                        .mapToInt(OrderItemDTO::getQuantity)
                        .sum();

                this.totalAmount = this.orderItems.stream()
                        .mapToDouble(item -> item.getFoodItemPrice() * item.getQuantity())
                        .sum();
            } catch (Exception e) {
                // If lazy loading exception occurs, log it
                System.err.println("Error processing order items for order " + order.getId() + ": " + e.getMessage());
            }
        }

        this.orderTime = order.getOrderTime();
        this.status = order.getStatus();
    }
}