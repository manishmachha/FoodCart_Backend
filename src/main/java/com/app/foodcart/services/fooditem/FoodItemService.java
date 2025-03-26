package com.app.foodcart.services.fooditem;

import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.foodcart.DTOs.FoodItemDTO;
import com.app.foodcart.DTOs.requests.FoodItemRequestDTO;
import com.app.foodcart.entities.FoodItem;
import com.app.foodcart.entities.Restaurant;
import com.app.foodcart.exceptions.DuplicateResourceException;
import com.app.foodcart.exceptions.ResourceNotFoundException;
import com.app.foodcart.repositories.FoodItemRepository;
import com.app.foodcart.repositories.RestaurantRepository;

@Service
@Transactional
public class FoodItemService {
    @Autowired
    private FoodItemRepository foodItemRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public FoodItem addFoodItem(FoodItemRequestDTO foodItem) {
        // Validate restaurant existence
        Restaurant restaurant = restaurantRepository.findById(foodItem.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", foodItem.getRestaurantId()));

        if (foodItemRepository.existsByNameAndRestaurantId(
                foodItem.getName(), foodItem.getRestaurantId())) {
            throw new DuplicateResourceException(
                    String.format("FoodItem already exists with name : '%s' in restaurant '%s'",
                            foodItem.getName(), restaurant.getName()));
        }

        FoodItem foodItemEntity = new FoodItem();
        foodItemEntity.setName(foodItem.getName());
        foodItemEntity.setPrice(foodItem.getPrice());
        byte[] imageBytes = Base64.getDecoder().decode(foodItem.getImage());
        foodItemEntity.setImage(imageBytes);
        foodItemEntity.setActive(foodItem.isActive());
        foodItemEntity.setRestaurant(restaurant);
        foodItemEntity.setActive(true);

        return foodItemRepository.save(foodItemEntity);
    }

    public FoodItemDTO getFoodItemById(Long id) {
        FoodItemDTO foodItemDTO = new FoodItemDTO(
                foodItemRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", id)));
        return foodItemDTO;
    }

    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    public List<FoodItem> getFoodItemsByRestaurantId(Long restaurantId) {
        // Check if restaurant exists
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant", "id", restaurantId);
        }
        return foodItemRepository.findByRestaurantId(restaurantId);
    }

    public FoodItemDTO updateFoodItem(Long id, FoodItemRequestDTO updatedFoodItem) {
        FoodItem existingFoodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", id));

        // Check for duplicate name if name is being changed
        if (!existingFoodItem.getName().equals(updatedFoodItem.getName()) &&
                foodItemRepository.existsByNameAndRestaurantId(
                        updatedFoodItem.getName(), existingFoodItem.getRestaurant().getId())) {
            throw new DuplicateResourceException(
                    String.format("FoodItem already exists with name : '%s' in restaurant '%s'",
                            updatedFoodItem.getName(), existingFoodItem.getRestaurant().getName()));
        }

        // Update fields
        existingFoodItem.setName(updatedFoodItem.getName());
        existingFoodItem.setPrice(updatedFoodItem.getPrice());
        existingFoodItem.setActive(updatedFoodItem.isActive());

        // Update image if provided
        if (updatedFoodItem.getImage() != null) {
            byte[] imageBytes = Base64.getDecoder().decode(updatedFoodItem.getImage());
            existingFoodItem.setImage(imageBytes);
        }

        return new FoodItemDTO(foodItemRepository.save(existingFoodItem));
    }

    public void deleteFoodItem(Long id) {
        // Check if food item exists
        if (!foodItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("FoodItem", "id", id);
        }
        foodItemRepository.deleteById(id);
    }
}
