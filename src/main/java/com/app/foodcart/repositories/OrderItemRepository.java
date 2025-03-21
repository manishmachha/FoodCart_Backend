package com.app.foodcart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.foodcart.entities.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
