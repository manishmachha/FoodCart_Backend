package com.app.foodcart.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "food_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_fooditem_name_restaurant", columnNames = { "name", "restaurant_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Food item name is required")
    @Size(min = 2, max = 100, message = "Food item name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isActive = true;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB", length = 1000000000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @NotNull(message = "Restaurant is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fooditem_restaurant"))
    private Restaurant restaurant;
}