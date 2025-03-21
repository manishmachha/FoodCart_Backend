package com.app.foodcart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.foodcart.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
