package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.Cart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long restaurantId;
    private String restaurantName;
    private List<CartItemDTO> cartItems;
    private LocalDateTime lastUpdated;
    private double totalAmount;

    public CartDTO(Cart cart) {
        this.id = cart.getId();

        if (cart.getUser() != null) {
            this.userId = cart.getUser().getId();
            this.userName = cart.getUser().getName();
        }

        if (cart.getRestaurant() != null) {
            this.restaurantId = cart.getRestaurant().getId();
            this.restaurantName = cart.getRestaurant().getName();
        }

        if (cart.getCartItems() != null) {
            this.cartItems = cart.getCartItems().stream()
                    .map(CartItemDTO::new)
                    .collect(Collectors.toList());
        }

        this.lastUpdated = cart.getLastUpdated();
        this.totalAmount = cart.getTotalAmount();
    }
}