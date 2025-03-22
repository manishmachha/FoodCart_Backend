package com.app.foodcart.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cart_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", foreignKey = @ForeignKey(name = "fk_cart_restaurant"))
    private Restaurant restaurant;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> cartItems;

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    // Calculated field for total amount
    @Transient
    public double getTotalAmount() {
        if (cartItems == null || cartItems.isEmpty()) {
            return 0.0;
        }
        return cartItems.stream()
                .mapToDouble(item -> item.getFoodItem().getPrice().doubleValue() * item.getQuantity())
                .sum();
    }

    // Method to clear cart items
    public void clearCart() {
        if (cartItems != null) {
            cartItems.clear();
        }
        this.lastUpdated = LocalDateTime.now();
    }
}