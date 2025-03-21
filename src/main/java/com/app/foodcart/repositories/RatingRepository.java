package com.app.foodcart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.foodcart.entities.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
