package com.app.foodcart.controllers.restaurant;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.foodcart.DTOs.ApiResponse;
import com.app.foodcart.DTOs.RestaurantDTO;
import com.app.foodcart.DTOs.requests.RestaurantRequestDTO;
import com.app.foodcart.entities.Restaurant;
import com.app.foodcart.exceptions.BadRequestException;
import com.app.foodcart.services.restaurant.RestaurantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Restaurant>> getRestaurantById(@PathVariable Long id) {
        // The service will throw ResourceNotFoundException if restaurant not found
        // which will be handled by the global exception handler
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        ApiResponse<Restaurant> response = ApiResponse.success(
                "Restaurant retrieved successfully", restaurant);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantDTO>>> getAllRestaurants() {
        List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants();
        ApiResponse<List<RestaurantDTO>> response = ApiResponse.success(
                "Restaurants retrieved successfully", restaurants);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Restaurant>> createRestaurant(
            @Valid @RequestBody RestaurantRequestDTO restaurantRequest) {
        try {
            // Convert RequestDTO to entity
            Restaurant restaurant = new Restaurant();
            restaurant.setName(restaurantRequest.getName());
            restaurant.setLocation(restaurantRequest.getLocation());
            restaurant.setPhoneNumber(restaurantRequest.getPhoneNumber());

            // The service will validate and throw appropriate exceptions
            // which will be handled by the global exception handler
            Restaurant createdRestaurant = restaurantService.createRestaurant(restaurant);
            ApiResponse<Restaurant> response = ApiResponse.created(
                    "Restaurant created successfully", createdRestaurant);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            if (e instanceof BadRequestException) {
                throw e;
            }
            throw new BadRequestException("Error creating restaurant: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Restaurant>> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequestDTO restaurantRequest) {
        try {
            // Convert RequestDTO to entity
            Restaurant restaurant = new Restaurant();
            restaurant.setName(restaurantRequest.getName());
            restaurant.setLocation(restaurantRequest.getLocation());
            restaurant.setPhoneNumber(restaurantRequest.getPhoneNumber());

            // The service will validate and throw appropriate exceptions
            // which will be handled by the global exception handler
            Restaurant updatedRestaurant = restaurantService.updateRestaurant(id, restaurant);
            ApiResponse<Restaurant> response = ApiResponse.success(
                    "Restaurant updated successfully", updatedRestaurant);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            if (e instanceof BadRequestException) {
                throw e;
            }
            throw new BadRequestException("Error updating restaurant: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteRestaurant(@PathVariable Long id) {
        // The service will throw ResourceNotFoundException if restaurant not found
        // which will be handled by the global exception handler
        restaurantService.deleteRestaurant(id);
        ApiResponse<?> response = ApiResponse.success("Restaurant deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
