package com.app.foodcart.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.foodcart.entities.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByName(String name);

    Optional<Restaurant> findByPhoneNumber(String phoneNumber);
}
