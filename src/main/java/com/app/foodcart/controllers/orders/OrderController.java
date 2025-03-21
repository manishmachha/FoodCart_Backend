package com.app.foodcart.controllers.orders;

import java.util.List;

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
import com.app.foodcart.entities.Order;
import com.app.foodcart.services.order.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        ApiResponse<Order> response = ApiResponse.success(
                String.format("Order #%d retrieved successfully", id), order);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        ApiResponse<List<Order>> response = ApiResponse.success(
                "All orders retrieved successfully", orders);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.placeOrder(order);
        ApiResponse<Order> response = ApiResponse.created(
                String.format("Order #%d created successfully", createdOrder.getId()), createdOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        Order updatedOrder = orderService.updateOrder(id, order);
        ApiResponse<Order> response = ApiResponse.success(
                String.format("Order #%d updated successfully", id), updatedOrder);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        ApiResponse<?> response = ApiResponse.success(
                String.format("Order #%d deleted successfully", id), null);
        return ResponseEntity.ok(response);
    }
}
