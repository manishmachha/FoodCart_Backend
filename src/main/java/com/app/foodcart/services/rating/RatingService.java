package com.app.foodcart.services.rating;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.foodcart.entities.Rating;
import com.app.foodcart.repositories.RatingRepository;

@Service
@Transactional
public class RatingService {
    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating addRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Rating getRatingById(Long id) {
        return ratingRepository.findById(id).orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Rating updateRating(Long id, Rating updatedRating) {
        Rating existingRating = getRatingById(id);
        existingRating.setUser(updatedRating.getUser());
        existingRating.setRestaurant(updatedRating.getRestaurant());
        existingRating.setFoodItem(updatedRating.getFoodItem());
        existingRating.setStars(updatedRating.getStars());
        existingRating.setComment(updatedRating.getComment());
        return ratingRepository.save(existingRating);
    }

    public void deleteRating(Long id) {
        ratingRepository.deleteById(id);
    }
}