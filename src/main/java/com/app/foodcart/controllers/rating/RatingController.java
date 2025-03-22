package com.app.foodcart.controllers.rating;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.foodcart.DTOs.ApiResponse;
import com.app.foodcart.DTOs.RatingDTO;
import com.app.foodcart.DTOs.requests.RatingRequestDTO;
import com.app.foodcart.entities.Rating;
import com.app.foodcart.entities.User;
import com.app.foodcart.services.rating.RatingService;
import com.app.foodcart.services.customUserDetails.CustomUserDetailsService;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CustomUserDetailsService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RatingDTO>> getRatingById(@PathVariable Long id) {
        Rating rating = ratingService.getRatingById(id);
        RatingDTO ratingDTO = new RatingDTO(rating);

        ApiResponse<RatingDTO> response = ApiResponse.success("Rating retrieved successfully", ratingDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RatingDTO>>> getAllRatings() {
        List<Rating> ratings = ratingService.getAllRatings();
        List<RatingDTO> ratingDTOs = ratings.stream()
                .map(RatingDTO::new)
                .collect(Collectors.toList());

        ApiResponse<List<RatingDTO>> response = ApiResponse.success("Ratings retrieved successfully", ratingDTOs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<RatingDTO>>> getCurrentUserRatings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserFromAuthentication(auth);

        List<Rating> ratings = ratingService.getRatingsByUser(user.getId());
        List<RatingDTO> ratingDTOs = ratings.stream()
                .map(RatingDTO::new)
                .collect(Collectors.toList());

        ApiResponse<List<RatingDTO>> response = ApiResponse.success("User ratings retrieved successfully", ratingDTOs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<RatingDTO>>> getRestaurantRatings(@PathVariable Long restaurantId) {
        List<Rating> ratings = ratingService.getRatingsByRestaurant(restaurantId);
        List<RatingDTO> ratingDTOs = ratings.stream()
                .map(RatingDTO::new)
                .collect(Collectors.toList());

        ApiResponse<List<RatingDTO>> response = ApiResponse.success("Restaurant ratings retrieved successfully",
                ratingDTOs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fooditem/{foodItemId}")
    public ResponseEntity<ApiResponse<List<RatingDTO>>> getFoodItemRatings(@PathVariable Long foodItemId) {
        List<Rating> ratings = ratingService.getRatingsByFoodItem(foodItemId);
        List<RatingDTO> ratingDTOs = ratings.stream()
                .map(RatingDTO::new)
                .collect(Collectors.toList());

        ApiResponse<List<RatingDTO>> response = ApiResponse.success("Food item ratings retrieved successfully",
                ratingDTOs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurant/{restaurantId}/average")
    public ResponseEntity<ApiResponse<Double>> getAverageRestaurantRating(@PathVariable Long restaurantId) {
        Double averageRating = ratingService.getAverageRatingForRestaurant(restaurantId);

        ApiResponse<Double> response = ApiResponse.success("Average restaurant rating retrieved successfully",
                averageRating != null ? averageRating : 0.0);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fooditem/{foodItemId}/average")
    public ResponseEntity<ApiResponse<Double>> getAverageFoodItemRating(@PathVariable Long foodItemId) {
        Double averageRating = ratingService.getAverageRatingForFoodItem(foodItemId);

        ApiResponse<Double> response = ApiResponse.success("Average food item rating retrieved successfully",
                averageRating != null ? averageRating : 0.0);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RatingDTO>> createRating(@Valid @RequestBody RatingRequestDTO ratingRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserFromAuthentication(auth);

        Rating rating = ratingService.rateRestaurantOrFoodItem(user.getId(), ratingRequest);
        RatingDTO ratingDTO = new RatingDTO(rating);

        ApiResponse<RatingDTO> response = ApiResponse.created("Rating created successfully", ratingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRating(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserFromAuthentication(auth);

        ratingService.deleteRating(id, user.getId());

        ApiResponse<Void> response = ApiResponse.success("Rating deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
