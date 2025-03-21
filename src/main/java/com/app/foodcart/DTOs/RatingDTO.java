package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.Rating;

@Data
public class RatingDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long restaurantId;
    private String restaurantName;
    private Long foodItemId;
    private String foodItemName;
    private int stars;
    private String comment;

    public RatingDTO(Rating rating) {
        this.id = rating.getId();

        if (rating.getUser() != null) {
            this.userId = rating.getUser().getId();
            this.userName = rating.getUser().getName();
        }

        if (rating.getRestaurant() != null) {
            this.restaurantId = rating.getRestaurant().getId();
            this.restaurantName = rating.getRestaurant().getName();
        }

        if (rating.getFoodItem() != null) {
            this.foodItemId = rating.getFoodItem().getId();
            this.foodItemName = rating.getFoodItem().getName();
        }

        this.stars = rating.getStars();
        this.comment = rating.getComment();
    }
}