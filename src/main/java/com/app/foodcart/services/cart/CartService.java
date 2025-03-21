package com.app.foodcart.services.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.foodcart.DTOs.requests.AddToCartRequestDTO;
import com.app.foodcart.DTOs.requests.CartItemRequestDTO;
import com.app.foodcart.entities.Cart;
import com.app.foodcart.entities.CartItem;
import com.app.foodcart.entities.FoodItem;
import com.app.foodcart.entities.Restaurant;
import com.app.foodcart.entities.User;
import com.app.foodcart.repositories.CartItemRepository;
import com.app.foodcart.repositories.CartRepository;
import com.app.foodcart.repositories.FoodItemRepository;
import com.app.foodcart.repositories.RestaurantRepository;
import com.app.foodcart.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    /**
     * Get cart for a user, create if not exists
     */
    public Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Optional<Cart> existingCart = cartRepository.findByUser(user);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        // Create new cart
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setLastUpdated(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    /**
     * Add items to cart
     */
    public Cart addToCart(Long userId, AddToCartRequestDTO request) {
        Cart cart = getOrCreateCart(userId);

        // If cart is already associated with a different restaurant, clear it
        if (request.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            if (cart.getRestaurant() != null && !cart.getRestaurant().getId().equals(restaurant.getId())) {
                clearCart(userId);
                cart = getOrCreateCart(userId);
            }

            cart.setRestaurant(restaurant);
        }

        // Add or update items
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (CartItemRequestDTO itemRequest : request.getItems()) {
                FoodItem foodItem = foodItemRepository.findById(itemRequest.getFoodItemId())
                        .orElseThrow(() -> new RuntimeException("Food item not found"));

                // Check if item already exists in cart
                Optional<CartItem> existingItem = cartItemRepository.findByCartAndFoodItem(cart, foodItem);

                if (existingItem.isPresent()) {
                    // Update quantity
                    CartItem item = existingItem.get();
                    item.setQuantity(itemRequest.getQuantity());
                    cartItemRepository.save(item);
                } else {
                    // Add new item
                    CartItem item = new CartItem();
                    item.setCart(cart);
                    item.setFoodItem(foodItem);
                    item.setQuantity(itemRequest.getQuantity());
                    cartItemRepository.save(item);
                }
            }
        }

        cart.setLastUpdated(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    /**
     * Get user's cart
     */
    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
    }

    /**
     * Update cart item quantity
     */
    public CartItem updateCartItemQuantity(Long userId, Long cartItemId, int quantity) {
        Cart cart = getCart(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Ensure the cart item belongs to the user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to user's cart");
        }

        cartItem.setQuantity(quantity);
        cart.setLastUpdated(LocalDateTime.now());

        cartRepository.save(cart);
        return cartItemRepository.save(cartItem);
    }

    /**
     * Remove item from cart
     */
    public void removeCartItem(Long userId, Long cartItemId) {
        Cart cart = getCart(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Ensure the cart item belongs to the user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to user's cart");
        }

        cartItemRepository.delete(cartItem);
        cart.setLastUpdated(LocalDateTime.now());
        cartRepository.save(cart);
    }

    /**
     * Clear cart
     */
    public void clearCart(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            // Delete all cart items
            cartItemRepository.deleteByCartId(cart.getId());

            // Reset cart
            cart.setRestaurant(null);
            cart.setLastUpdated(LocalDateTime.now());
            cartRepository.save(cart);
        }
    }
}