package com.app.foodcart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.foodcart.entities.FoodItem;
import com.app.foodcart.entities.Restaurant;

import java.util.List;
import java.util.Optional;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    /**
     * Find a food item by name and restaurant
     */
    Optional<FoodItem> findByNameAndRestaurant(String name, Restaurant restaurant);

    /**
     * Find a food item by name and restaurant ID
     */
    @Query("SELECT f FROM FoodItem f WHERE f.name = :name AND f.restaurant.id = :restaurantId")
    Optional<FoodItem> findByNameAndRestaurantId(@Param("name") String name, @Param("restaurantId") Long restaurantId);

    /**
     * Find all food items by restaurant
     */
    List<FoodItem> findByRestaurant(Restaurant restaurant);

    /**
     * Find all food items by restaurant ID
     */
    List<FoodItem> findByRestaurantId(Long restaurantId);

    /**
     * Check if a food item with the same name already exists for a restaurant
     */
    boolean existsByNameAndRestaurantId(String name, Long restaurantId);
}
