package com.app.foodcart.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "order_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_orderitem_order_fooditem", columnNames = { "order_id", "food_item_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "order" })
@EqualsAndHashCode(exclude = { "order" })
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Order is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orderitem_order"))
    @JsonBackReference
    private Order order;

    @NotNull(message = "Food item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orderitem_fooditem"))
    private FoodItem foodItem;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private int quantity = 1;

    @NotNull(message = "Item price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Item price must be greater than 0")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal itemPrice;

    // Helper method to get subtotal
    @Transient
    public BigDecimal getSubtotal() {
        return itemPrice != null ? itemPrice.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
    }
}
