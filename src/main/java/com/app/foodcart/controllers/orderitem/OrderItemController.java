package com.app.foodcart.controllers.orderitem;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.foodcart.DTOs.ApiResponse;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> getOrderItemById(@PathVariable Long id) {
        String data = "OrderItem details for ID: " + id;
        ApiResponse<String> response = ApiResponse.success(
                "Order item retrieved successfully", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createOrderItem() {
        String data = "OrderItem created";
        ApiResponse<String> response = ApiResponse.created(
                "Order item created successfully", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateOrderItem(@PathVariable Long id) {
        String data = "OrderItem updated for ID: " + id;
        ApiResponse<String> response = ApiResponse.success(
                "Order item updated successfully", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrderItem(@PathVariable Long id) {
        String data = "OrderItem deleted for ID: " + id;
        ApiResponse<String> response = ApiResponse.success(
                "Order item deleted successfully", data);
        return ResponseEntity.ok(response);
    }
}