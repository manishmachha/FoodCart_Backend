package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.Cart;
import com.app.foodcart.entities.Restaurant;
import com.app.foodcart.entities.CartItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private String userName;
    private List<RestaurantItemsDTO> restaurantItems; // Group items by restaurant
    private List<CartItemDTO> cartItems;
    private LocalDateTime lastUpdated;
    private double totalAmount;

    public CartDTO(Cart cart) {
        this.id = cart.getId();

        if (cart.getUser() != null) {
            this.userId = cart.getUser().getId();
            this.userName = cart.getUser().getName();
        }

        if (cart.getCartItems() != null) {
            this.cartItems = cart.getCartItems().stream()
                    .map(CartItemDTO::new)
                    .collect(Collectors.toList());

            // Group cart items by restaurant
            Map<Restaurant, List<CartItem>> itemsByRestaurant = cart.getItemsByRestaurant();
            this.restaurantItems = new ArrayList<>();

            itemsByRestaurant.forEach((restaurant, items) -> {
                RestaurantItemsDTO restaurantItemsDTO = new RestaurantItemsDTO();
                restaurantItemsDTO.setRestaurantId(restaurant.getId());
                restaurantItemsDTO.setRestaurantName(restaurant.getName());

                List<CartItemDTO> restaurantCartItems = items.stream()
                        .map(CartItemDTO::new)
                        .collect(Collectors.toList());
                restaurantItemsDTO.setItems(restaurantCartItems);

                // Calculate subtotal for this restaurant
                double subtotal = items.stream()
                        .mapToDouble(item -> item.getFoodItem().getPrice().doubleValue() * item.getQuantity())
                        .sum();
                restaurantItemsDTO.setSubtotal(subtotal);

                this.restaurantItems.add(restaurantItemsDTO);
            });
        } else {
            this.cartItems = new ArrayList<>();
            this.restaurantItems = new ArrayList<>();
        }

        this.lastUpdated = cart.getLastUpdated();
        this.totalAmount = cart.getTotalAmount();
    }

    // Inner class to represent items from a single restaurant
    @Data
    public static class RestaurantItemsDTO {
        private Long restaurantId;
        private String restaurantName;
        private List<CartItemDTO> items;
        private double subtotal;
    }
}