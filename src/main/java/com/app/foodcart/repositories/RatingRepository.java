package com.app.foodcart.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.foodcart.entities.FoodItem;
import com.app.foodcart.entities.Rating;
import com.app.foodcart.entities.Restaurant;
import com.app.foodcart.entities.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * Find ratings by user
     */
    List<Rating> findByUser(User user);

    /**
     * Find ratings by restaurant
     */
    List<Rating> findByRestaurant(Restaurant restaurant);

    /**
     * Find ratings by food item
     */
    List<Rating> findByFoodItem(FoodItem foodItem);

    /**
     * Find rating by user and restaurant
     */
    Optional<Rating> findByUserAndRestaurant(User user, Restaurant restaurant);

    /**
     * Find rating by user and food item
     */
    Optional<Rating> findByUserAndFoodItem(User user, FoodItem foodItem);

    /**
     * Get average rating for a restaurant
     */
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.restaurant = :restaurant")
    Double getAverageRatingForRestaurant(@Param("restaurant") Restaurant restaurant);

    /**
     * Get average rating for a food item
     */
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.foodItem = :foodItem")
    Double getAverageRatingForFoodItem(@Param("foodItem") FoodItem foodItem);

    /**
     * Check if user has ordered from restaurant
     */
    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.user.id = :userId AND o.restaurant.id = :restaurantId")
    boolean hasUserOrderedFromRestaurant(@Param("userId") Long userId, @Param("restaurantId") Long restaurantId);

    /**
     * Check if user has ordered food item
     */
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi JOIN oi.order o WHERE o.user.id = :userId AND oi.foodItem.id = :foodItemId")
    boolean hasUserOrderedFoodItem(@Param("userId") Long userId, @Param("foodItemId") Long foodItemId);
}
