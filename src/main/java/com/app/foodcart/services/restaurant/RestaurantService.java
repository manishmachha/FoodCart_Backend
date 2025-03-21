package com.app.foodcart.services.restaurant;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.foodcart.DTOs.RestaurantDTO;
import com.app.foodcart.entities.Restaurant;
import com.app.foodcart.exceptions.DuplicateResourceException;
import com.app.foodcart.exceptions.ResourceNotFoundException;
import com.app.foodcart.repositories.RestaurantRepository;

@Service
@Transactional
public class RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
    }

    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(RestaurantDTO::new)
                .collect(Collectors.toList());
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        // Check if a restaurant with the same name exists
        restaurantRepository.findByName(restaurant.getName()).ifPresent(r -> {
            throw new DuplicateResourceException("Restaurant", "name", restaurant.getName());
        });

        // Check if a restaurant with the same phone number exists
        if (restaurant.getPhoneNumber() != null) {
            restaurantRepository
                    .findByPhoneNumber(restaurant.getPhoneNumber()).ifPresent(r -> {
                        throw new DuplicateResourceException("Restaurant", "phone number", restaurant.getPhoneNumber());
                    });
        }

        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Long id, Restaurant updatedRestaurant) {
        Restaurant existingRestaurant = getRestaurantById(id);

        // Check if a different restaurant with the same name exists
        Optional<Restaurant> restaurantWithSameName = restaurantRepository.findByName(updatedRestaurant.getName());
        if (restaurantWithSameName.isPresent() && !restaurantWithSameName.get().getId().equals(id)) {
            throw new DuplicateResourceException("Restaurant", "name", updatedRestaurant.getName());
        }

        // Check if a different restaurant with the same phone number exists
        if (updatedRestaurant.getPhoneNumber() != null) {
            Optional<Restaurant> restaurantWithSamePhone = restaurantRepository
                    .findByPhoneNumber(updatedRestaurant.getPhoneNumber());
            if (restaurantWithSamePhone.isPresent() && !restaurantWithSamePhone.get().getId().equals(id)) {
                throw new DuplicateResourceException("Restaurant", "phone number", updatedRestaurant.getPhoneNumber());
            }
        }

        // Update fields
        existingRestaurant.setName(updatedRestaurant.getName());
        existingRestaurant.setLocation(updatedRestaurant.getLocation());

        // Only update phoneNumber if provided
        if (updatedRestaurant.getPhoneNumber() != null) {
            existingRestaurant.setPhoneNumber(updatedRestaurant.getPhoneNumber());
        }

        // Only update foodItems if provided
        if (updatedRestaurant.getFoodItems() != null) {
            existingRestaurant.setFoodItems(updatedRestaurant.getFoodItems());
        }

        return restaurantRepository.save(existingRestaurant);
    }

    public void deleteRestaurant(Long id) {
        // Check if restaurant exists
        getRestaurantById(id); // Will throw ResourceNotFoundException if not found
        restaurantRepository.deleteById(id);
    }
}