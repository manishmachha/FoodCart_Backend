package com.app.foodcart.services.rating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.foodcart.DTOs.requests.RatingRequestDTO;
import com.app.foodcart.entities.FoodItem;
import com.app.foodcart.entities.Rating;
import com.app.foodcart.entities.Restaurant;
import com.app.foodcart.entities.User;
import com.app.foodcart.exceptions.BadRequestException;
import com.app.foodcart.exceptions.ResourceNotFoundException;
import com.app.foodcart.exceptions.UnauthorizedException;
import com.app.foodcart.repositories.FoodItemRepository;
import com.app.foodcart.repositories.RatingRepository;
import com.app.foodcart.repositories.RestaurantRepository;
import com.app.foodcart.repositories.UserRepository;

@Service
@Transactional
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    /**
     * Add a new rating or update an existing one
     */
    @Transactional
    public Rating rateRestaurantOrFoodItem(Long userId, RatingRequestDTO ratingRequest) {
        if (ratingRequest.getRestaurantId() == null && ratingRequest.getFoodItemId() == null) {
            throw new BadRequestException("Either restaurant ID or food item ID must be provided");
        }

        if (ratingRequest.getRestaurantId() != null && ratingRequest.getFoodItemId() != null) {
            throw new BadRequestException("Cannot rate both restaurant and food item at the same time");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Rating rating;

        if (ratingRequest.getRestaurantId() != null) {
            // Rating a restaurant
            Restaurant restaurant = restaurantRepository.findById(ratingRequest.getRestaurantId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Restaurant", "id", ratingRequest.getRestaurantId()));

            // Check if user has ordered from this restaurant
            if (!ratingRepository.hasUserOrderedFromRestaurant(userId, ratingRequest.getRestaurantId())) {
                throw new UnauthorizedException("You can only rate restaurants you have ordered from");
            }

            // Check if user has already rated this restaurant
            Optional<Rating> existingRating = ratingRepository.findByUserAndRestaurant(user, restaurant);

            if (existingRating.isPresent()) {
                // Update existing rating
                rating = existingRating.get();
                rating.setStars(ratingRequest.getStars());
                rating.setComment(ratingRequest.getComment());
                rating.setRatedAt(LocalDateTime.now());
            } else {
                // Create new rating
                rating = new Rating();
                rating.setUser(user);
                rating.setRestaurant(restaurant);
                rating.setStars(ratingRequest.getStars());
                rating.setComment(ratingRequest.getComment());
                rating.setRatedAt(LocalDateTime.now());
            }
        } else {
            // Rating a food item
            FoodItem foodItem = foodItemRepository.findById(ratingRequest.getFoodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food item", "id", ratingRequest.getFoodItemId()));

            // Check if user has ordered this food item
            if (!ratingRepository.hasUserOrderedFoodItem(userId, ratingRequest.getFoodItemId())) {
                throw new UnauthorizedException("You can only rate food items you have ordered");
            }

            // Check if user has already rated this food item
            Optional<Rating> existingRating = ratingRepository.findByUserAndFoodItem(user, foodItem);

            if (existingRating.isPresent()) {
                // Update existing rating
                rating = existingRating.get();
                rating.setStars(ratingRequest.getStars());
                rating.setComment(ratingRequest.getComment());
                rating.setRatedAt(LocalDateTime.now());
            } else {
                // Create new rating
                rating = new Rating();
                rating.setUser(user);
                rating.setFoodItem(foodItem);
                rating.setStars(ratingRequest.getStars());
                rating.setComment(ratingRequest.getComment());
                rating.setRatedAt(LocalDateTime.now());
            }
        }

        return ratingRepository.save(rating);
    }

    /**
     * Get a rating by its ID
     */
    public Rating getRatingById(Long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating", "id", id));
    }

    /**
     * Get all ratings in the system
     */
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    /**
     * Get all ratings by a specific user
     */
    public List<Rating> getRatingsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return ratingRepository.findByUser(user);
    }

    /**
     * Get all ratings for a specific restaurant
     */
    public List<Rating> getRatingsByRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId));
        return ratingRepository.findByRestaurant(restaurant);
    }

    /**
     * Get all ratings for a specific food item
     */
    public List<Rating> getRatingsByFoodItem(Long foodItemId) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item", "id", foodItemId));
        return ratingRepository.findByFoodItem(foodItem);
    }

    /**
     * Get average rating for a restaurant
     */
    public Double getAverageRatingForRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId));
        return ratingRepository.getAverageRatingForRestaurant(restaurant);
    }

    /**
     * Get average rating for a food item
     */
    public Double getAverageRatingForFoodItem(Long foodItemId) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item", "id", foodItemId));
        return ratingRepository.getAverageRatingForFoodItem(foodItem);
    }

    /**
     * Delete a rating
     */
    public void deleteRating(Long id, Long userId) {
        Rating rating = getRatingById(id);

        // Only the user who created the rating can delete it
        if (!rating.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own ratings");
        }

        ratingRepository.deleteById(id);
    }
}