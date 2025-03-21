package com.app.foodcart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.foodcart.entities.CartItem;
import com.app.foodcart.entities.FoodItem;
import com.app.foodcart.entities.Cart;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartAndFoodItem(Cart cart, FoodItem foodItem);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.foodItem.id = :foodItemId")
    Optional<CartItem> findByCartIdAndFoodItemId(@Param("cartId") Long cartId, @Param("foodItemId") Long foodItemId);

    void deleteByCartId(Long cartId);
}