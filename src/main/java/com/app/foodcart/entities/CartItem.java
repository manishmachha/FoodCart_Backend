package com.app.foodcart.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cartitem_cart_fooditem", columnNames = { "cart_id", "food_item_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Cart is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cartitem_cart"))
    private Cart cart;

    @NotNull(message = "Food item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cartitem_fooditem"))
    private FoodItem foodItem;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private int quantity = 1;

    // Helper method to get subtotal
    @Transient
    public double getSubtotal() {
        return foodItem != null ? foodItem.getPrice().doubleValue() * quantity : 0;
    }
}