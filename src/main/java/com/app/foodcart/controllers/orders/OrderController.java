package com.app.foodcart.controllers.orders;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.foodcart.DTOs.ApiResponse;
import com.app.foodcart.DTOs.OrderDTO;
import com.app.foodcart.DTOs.requests.OrderRequestDTO;
import com.app.foodcart.DTOs.requests.UpdateOrderRequestDTO;
import com.app.foodcart.entities.Order;
import com.app.foodcart.entities.User;
import com.app.foodcart.services.customUserDetails.CustomUserDetailsService;
import com.app.foodcart.services.order.OrderService;
import com.app.foodcart.services.bill.BillService;
import com.app.foodcart.DTOs.BillDTO;
import com.app.foodcart.entities.Bill;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private BillService billService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserFromAuthentication(auth);

        Order order = orderService.getOrderById(id, user.getId());
        OrderDTO orderDTO = new OrderDTO(order);

        ApiResponse<OrderDTO> response = ApiResponse.success(
                String.format("Order #%d retrieved successfully", id), orderDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getUserOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserFromAuthentication(auth);

        List<Order> orders = orderService.getUserOrders(user.getId());
        List<OrderDTO> orderDTOs = orders.stream().map(OrderDTO::new).toList();

        ApiResponse<List<OrderDTO>> response = ApiResponse.success(
                "User orders retrieved successfully", orderDTOs);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserFromAuthentication(auth);

        // Get orders with items already loaded
        List<Order> createdOrders = orderService.createOrdersFromMultipleRestaurants(user.getId(), orderRequest);

        if (createdOrders.size() == 1) {
            OrderDTO orderDTO = new OrderDTO(createdOrders.get(0));

            // Get the bill for this order
            Bill bill = billService.generateBillForOrder(createdOrders.get(0).getId());
            BillDTO billDTO = new BillDTO(bill);

            // Create a response with both order and bill
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("order", orderDTO);
            responseData.put("bill", billDTO);

            ApiResponse<Map<String, Object>> response = ApiResponse.created(
                    String.format("Order #%d created successfully with bill", createdOrders.get(0).getId()),
                    responseData);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            List<OrderDTO> orderDTOs = createdOrders.stream()
                    .map(OrderDTO::new)
                    .toList();

            // Get bills for all orders
            List<Bill> bills = createdOrders.stream()
                    .map(order -> billService.generateBillForOrder(order.getId()))
                    .toList();
            List<BillDTO> billDTOs = bills.stream()
                    .map(BillDTO::new)
                    .toList();

            // Create a response with both orders and bills
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("orders", orderDTOs);
            responseData.put("bills", billDTOs);

            ApiResponse<Map<String, Object>> response = ApiResponse.created(
                    String.format("Created %d orders from different restaurants with bills", createdOrders.size()),
                    responseData);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequestDTO updateRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserFromAuthentication(auth);

        Order updatedOrder = orderService.updateOrder(id, user.getId(), updateRequest);
        OrderDTO orderDTO = new OrderDTO(updatedOrder);

        ApiResponse<OrderDTO> response = ApiResponse.success(
                String.format("Order #%d updated successfully", id), orderDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteOrder(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserFromAuthentication(auth);

        orderService.deleteOrder(id, user.getId());

        ApiResponse<?> response = ApiResponse.success(
                String.format("Order #%d deleted successfully", id), null);
        return ResponseEntity.ok(response);
    }
}
