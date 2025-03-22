package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.CartItem;

@Data
public class CartItemDTO {
    private Long id;
    private Long cartId;
    private Long foodItemId;
    private String foodItemName;
    private double foodItemPrice;
    private int quantity;
    private double subtotal;
    private Long restaurantId;
    private String restaurantName;

    public CartItemDTO(CartItem cartItem) {
        this.id = cartItem.getId();

        if (cartItem.getCart() != null) {
            this.cartId = cartItem.getCart().getId();
        }

        if (cartItem.getFoodItem() != null) {
            this.foodItemId = cartItem.getFoodItem().getId();
            this.foodItemName = cartItem.getFoodItem().getName();
            this.foodItemPrice = cartItem.getFoodItem().getPrice().doubleValue();

            // Add restaurant information
            if (cartItem.getFoodItem().getRestaurant() != null) {
                this.restaurantId = cartItem.getFoodItem().getRestaurant().getId();
                this.restaurantName = cartItem.getFoodItem().getRestaurant().getName();
            }
        }

        this.quantity = cartItem.getQuantity();
        this.subtotal = cartItem.getSubtotal();
    }
}