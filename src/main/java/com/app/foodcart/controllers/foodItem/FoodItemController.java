package com.app.foodcart.controllers.foodItem;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.foodcart.DTOs.ApiResponse;
import com.app.foodcart.DTOs.FoodItemDTO;
import com.app.foodcart.DTOs.requests.FoodItemRequestDTO;
import com.app.foodcart.entities.FoodItem;
import com.app.foodcart.services.fooditem.FoodItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/foods")
public class FoodItemController {
    @Autowired
    private FoodItemService foodItemService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodItemDTO>> getFoodItemById(@PathVariable Long id) {
        FoodItemDTO foodItemDTO = foodItemService.getFoodItemById(id);
        ApiResponse<FoodItemDTO> response = ApiResponse.success(
                String.format("Food item with ID %d found", id), foodItemDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodItemDTO>>> getAllFoodItems() {
        List<FoodItem> foodItems = foodItemService.getAllFoodItems();
        List<FoodItemDTO> foodItemDTOs = foodItems.stream().map(FoodItemDTO::new).collect(Collectors.toList());

        ApiResponse<List<FoodItemDTO>> response = ApiResponse.success(
                String.format("Found %d food items", foodItemDTOs.size()), foodItemDTOs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<FoodItemDTO>>> getFoodItemsByRestaurant(@PathVariable Long restaurantId) {
        List<FoodItem> foodItems = foodItemService.getFoodItemsByRestaurantId(restaurantId);
        List<FoodItemDTO> foodItemDTOs = foodItems.stream().map(FoodItemDTO::new).collect(Collectors.toList());

        ApiResponse<List<FoodItemDTO>> response = ApiResponse.success(
                String.format("Found %d food items for restaurant ID %d",
                        foodItemDTOs.size(), restaurantId),
                foodItemDTOs);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodItemDTO>> addFoodItem(
            @Valid @RequestBody FoodItemRequestDTO foodItemRequest) {
        FoodItem createdItem = foodItemService.addFoodItem(foodItemRequest);
        FoodItemDTO foodItemDTO = new FoodItemDTO(createdItem);

        ApiResponse<FoodItemDTO> response = ApiResponse.created(
                "Food item created successfully", foodItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodItemDTO>> updateFoodItem(
            @PathVariable Long id,
            @Valid @RequestBody FoodItemRequestDTO foodItemRequest) {

        FoodItemDTO updatedItem = foodItemService.updateFoodItem(id, foodItemRequest);

        ApiResponse<FoodItemDTO> response = ApiResponse.success(
                String.format("Food item with ID %d updated successfully", id), updatedItem);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteFoodItem(@PathVariable Long id) {
        foodItemService.deleteFoodItem(id);

        ApiResponse<?> response = ApiResponse.deleted();
        return ResponseEntity.ok(response);
    }
}