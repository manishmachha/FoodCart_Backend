package com.app.foodcart.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFoodItemException extends RuntimeException {

    public InvalidFoodItemException(String message) {
        super(message);
    }

    public InvalidFoodItemException(String foodItemName, Long restaurantId) {
        super(String.format("Food item '%s' does not belong to restaurant with id: %d", foodItemName, restaurantId));
    }
}