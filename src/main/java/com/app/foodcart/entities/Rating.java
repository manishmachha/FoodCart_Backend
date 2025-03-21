package com.app.foodcart.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(name = "uk_rating_user_restaurant", columnNames = { "user_id", "restaurant_id" }),
        @UniqueConstraint(name = "uk_rating_user_fooditem", columnNames = { "user_id", "food_item_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rating_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", foreignKey = @ForeignKey(name = "fk_rating_restaurant"))
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", foreignKey = @ForeignKey(name = "fk_rating_fooditem"))
    private FoodItem foodItem;

    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    @Column(nullable = false)
    private int stars;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    @Column(length = 500)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime ratedAt = LocalDateTime.now();

    @AssertTrue(message = "Either restaurant or food item must be rated, not both or neither")
    private boolean isEitherRestaurantOrFoodItemRated() {
        return (restaurant != null && foodItem == null) || (restaurant == null && foodItem != null);
    }
}
