package com.app.foodcart.controllers.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.app.foodcart.DTOs.ApiResponse;
import com.app.foodcart.DTOs.CartDTO;
import com.app.foodcart.DTOs.CartItemDTO;
import com.app.foodcart.DTOs.requests.AddToCartRequestDTO;
import com.app.foodcart.entities.Cart;
import com.app.foodcart.entities.CartItem;
import com.app.foodcart.entities.User;
import com.app.foodcart.services.cart.CartService;
import com.app.foodcart.services.customUserDetails.CustomUserDetailsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Get current user's cart
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCurrentUserCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserFromAuthentication(auth);

        Cart cart = cartService.getCart(user.getId());
        CartDTO cartDTO = new CartDTO(cart);

        ApiResponse<CartDTO> response = ApiResponse.success(
                "Cart retrieved successfully", cartDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Add items to cart
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(@Valid @RequestBody AddToCartRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserFromAuthentication(auth);

        Cart cart = cartService.addToCart(user.getId(), request);
        CartDTO cartDTO = new CartDTO(cart);

        ApiResponse<CartDTO> response = ApiResponse.created(
                "Items added to cart successfully", cartDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update cart item quantity
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartItemDTO>> updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserFromAuthentication(auth);

        CartItem updatedItem = cartService.updateCartItemQuantity(user.getId(), itemId, quantity);
        CartItemDTO itemDTO = new CartItemDTO(updatedItem);

        ApiResponse<CartItemDTO> response = ApiResponse.success(
                String.format("Cart item %d quantity updated to %d", itemId, quantity),
                itemDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> removeCartItem(@PathVariable Long itemId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserFromAuthentication(auth);

        cartService.removeCartItem(user.getId(), itemId);

        ApiResponse<?> response = ApiResponse.success(
                String.format("Item %d removed from cart", itemId), null);
        return ResponseEntity.ok(response);
    }

    /**
     * Clear cart
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<?>> clearCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserFromAuthentication(auth);

        cartService.clearCart(user.getId());

        ApiResponse<?> response = ApiResponse.success("Cart cleared successfully", null);
        return ResponseEntity.ok(response);
    }
}