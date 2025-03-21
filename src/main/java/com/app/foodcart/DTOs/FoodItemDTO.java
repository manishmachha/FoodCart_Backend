package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.FoodItem;
import java.math.BigDecimal;
import java.util.Base64;

@Data
public class FoodItemDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String image;
    private Long restaurantId;
    private String restaurantName;
    private boolean isActive;

    public FoodItemDTO(FoodItem foodItem) {
        this.id = foodItem.getId();
        this.name = foodItem.getName();
        this.price = foodItem.getPrice();
        this.isActive = foodItem.isActive();

        // Convert image bytes to Base64 if present
        if (foodItem.getImage() != null) {
            this.image = Base64.getEncoder().encodeToString(foodItem.getImage());
        }

        if (foodItem.getRestaurant() != null) {
            this.restaurantId = foodItem.getRestaurant().getId();
            this.restaurantName = foodItem.getRestaurant().getName();
        }
    }
}