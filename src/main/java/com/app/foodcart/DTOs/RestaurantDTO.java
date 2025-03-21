package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.Restaurant;

@Data
public class RestaurantDTO {
    private Long id;
    private String name;
    private String location;
    private String phoneNumber;
    public RestaurantDTO(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.location = restaurant.getLocation();
        this.phoneNumber = restaurant.getPhoneNumber();
    }
}