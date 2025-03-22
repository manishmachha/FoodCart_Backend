package com.app.foodcart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import com.app.foodcart.entities.Order;
import com.app.foodcart.entities.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderTimeDesc(User user);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.foodItem LEFT JOIN FETCH o.user LEFT JOIN FETCH o.restaurant WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.foodItem LEFT JOIN FETCH o.restaurant WHERE o.user = :user ORDER BY o.orderTime DESC")
    List<Order> findByUserWithItemsOrderByOrderTimeDesc(@Param("user") User user);
}
