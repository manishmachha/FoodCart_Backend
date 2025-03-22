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
import com.app.foodcart.exceptions.BadRequestException;
import com.app.foodcart.exceptions.InvalidFoodItemException;
import com.app.foodcart.exceptions.ResourceNotFoundException;
import com.app.foodcart.repositories.CartItemRepository;
import com.app.foodcart.repositories.CartRepository;
import com.app.foodcart.repositories.FoodItemRepository;
import com.app.foodcart.repositories.RestaurantRepository;
import com.app.foodcart.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
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
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

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
    @Transactional
    public Cart addToCart(Long userId, AddToCartRequestDTO request) {
        Cart cart = getOrCreateCart(userId);

        // Validate restaurant exists (but don't clear cart for different restaurants)
        if (request.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId()));

            // We no longer clear the cart when switching restaurants
            // No need to set cart.restaurant as it can have items from multiple restaurants
        }

        // Add or update items
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (CartItemRequestDTO itemRequest : request.getItems()) {
                // Explicitly check for null foodItemId before using it
                if (itemRequest.getFoodItemId() == null) {
                    throw new BadRequestException("Food item ID is required and cannot be null");
                }

                FoodItem foodItem = foodItemRepository.findById(itemRequest.getFoodItemId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Food item", "id", itemRequest.getFoodItemId()));

                // Validate that the food item belongs to the specified restaurant
                if (request.getRestaurantId() != null &&
                        !foodItem.getRestaurant().getId().equals(request.getRestaurantId())) {
                    throw new InvalidFoodItemException(foodItem.getName(), request.getRestaurantId());
                }

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
        cartRepository.flush();
        return cartRepository.save(cart);
    }

    /**
     * Get user's cart
     */
    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
    }

    /**
     * Update cart item quantity
     */
    public CartItem updateCartItemQuantity(Long userId, Long cartItemId, int quantity) {
        Cart cart = getCart(userId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", cartItemId));

        // Ensure the cart item belongs to the user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to user's cart");
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
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", cartItemId));

        // Ensure the cart item belongs to the user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to user's cart");
        }

        cartItemRepository.delete(cartItem);
        cart.setLastUpdated(LocalDateTime.now());
        cartRepository.save(cart);
    }

    /**
     * Clear cart
     */
    @Transactional
    public void clearCart(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();

            // Get cart items from repository directly to ensure we have the latest state
            List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

            // Delete each item individually to avoid persistence issues
            for (CartItem item : items) {
                cartItemRepository.delete(item);
            }
            cartItemRepository.flush();

            // Reset cart
            cart.setLastUpdated(LocalDateTime.now());
            cartRepository.save(cart);
            cartRepository.flush();
        }
    }
}