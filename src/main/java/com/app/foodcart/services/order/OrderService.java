package com.app.foodcart.services.order;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import com.app.foodcart.DTOs.requests.OrderRequestDTO;
import com.app.foodcart.DTOs.requests.OrderItemRequestDTO;
import com.app.foodcart.DTOs.requests.UpdateOrderRequestDTO;
import com.app.foodcart.entities.Order;
import com.app.foodcart.entities.OrderItem;
import com.app.foodcart.entities.User;
import com.app.foodcart.entities.FoodItem;
import com.app.foodcart.entities.Restaurant;
import com.app.foodcart.entities.Address;
import com.app.foodcart.entities.Bill;
import com.app.foodcart.entities.enums.OrderStatus;
import com.app.foodcart.exceptions.BadRequestException;
import com.app.foodcart.exceptions.ResourceNotFoundException;
import com.app.foodcart.exceptions.UnauthorizedException;
import com.app.foodcart.repositories.OrderRepository;
import com.app.foodcart.repositories.OrderItemRepository;
import com.app.foodcart.repositories.UserRepository;
import com.app.foodcart.repositories.FoodItemRepository;
import com.app.foodcart.repositories.RestaurantRepository;
import com.app.foodcart.repositories.AddressRepository;
import com.app.foodcart.services.bill.BillService;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BillService billService;

    /**
     * Get order by ID, ensuring it belongs to the specified user
     */
    public Order getOrderById(Long id, Long userId) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        // Verify order belongs to the user
        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to access this order");
        }

        return order;
    }

    /**
     * Get all orders for a specific user
     */
    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return orderRepository.findByUserWithItemsOrderByOrderTimeDesc(user);
    }

    /**
     * Create orders from multiple restaurants and return all of them
     */
    @Transactional
    public List<Order> createOrdersFromMultipleRestaurants(Long userId, OrderRequestDTO orderRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // This validation is now handled by the @NotEmpty annotation in OrderRequestDTO
        // Keeping it for backward compatibility with non-annotated requests
        if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one food item");
        }

        // Find the delivery address
        Address deliveryAddress;
        if (orderRequest.getDeliveryAddressId() != null) {
            deliveryAddress = addressRepository.findById(orderRequest.getDeliveryAddressId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Address", "id", orderRequest.getDeliveryAddressId()));

            // Verify address belongs to the user
            if (!deliveryAddress.getUser().getId().equals(userId)) {
                throw new UnauthorizedException("You don't have permission to use this address");
            }
        } else {
            // Use first address if available
            List<Address> userAddresses = user.getAddresses();
            if (userAddresses == null || userAddresses.isEmpty()) {
                throw new BadRequestException("User must have at least one address to place an order");
            }
            deliveryAddress = userAddresses.get(0);
        }

        // Group items by restaurant
        Map<Long, List<OrderItemRequestDTO>> itemsByRestaurant = new HashMap<>();

        // First, fetch all food items to validate and group by restaurant
        for (OrderItemRequestDTO itemRequest : orderRequest.getOrderItems()) {
            FoodItem foodItem = foodItemRepository.findById(itemRequest.getFoodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food item", "id", itemRequest.getFoodItemId()));

            Long restaurantId = foodItem.getRestaurant().getId();
            itemsByRestaurant.computeIfAbsent(restaurantId, k -> new ArrayList<>())
                    .add(itemRequest);
        }

        // Create one order per restaurant
        List<Order> createdOrders = new ArrayList<>();

        for (Map.Entry<Long, List<OrderItemRequestDTO>> entry : itemsByRestaurant.entrySet()) {
            Long restaurantId = entry.getKey();
            List<OrderItemRequestDTO> restaurantItems = entry.getValue();

            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId));

            // Create the order
            Order order = new Order();
            order.setUser(user);
            order.setRestaurant(restaurant);
            order.setOrderTime(LocalDateTime.now());
            order.setStatus(OrderStatus.CONFIRMED);
            order.setDeliveryAddress(deliveryAddress);

            // Save the order first to get an ID
            order = orderRepository.save(order);
            final Long orderId = order.getId(); // Create a final copy for later use

            // Process each order item separately to avoid circular references
            for (OrderItemRequestDTO itemRequest : restaurantItems) {
                FoodItem foodItem = foodItemRepository.findById(itemRequest.getFoodItemId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Food item", "id", itemRequest.getFoodItemId()));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setFoodItem(foodItem);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setItemPrice(foodItem.getPrice());

                // Save the item
                orderItemRepository.save(orderItem);
            }

            // Clear persistence context to avoid circular reference issues
            entityManager.flush();
            entityManager.clear();

            // Fetch a completely fresh copy of the order with all its related entities
            Order freshOrder = orderRepository.findByIdWithItems(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

            // Force initialization of the user and restaurant
            if (freshOrder.getUser() != null) {
                freshOrder.getUser().getName(); // Force initialization
            }

            if (freshOrder.getRestaurant() != null) {
                freshOrder.getRestaurant().getName(); // Force initialization
            }

            // Force initialization of all order items and their food items
            if (freshOrder.getOrderItems() != null) {
                freshOrder.getOrderItems().forEach(item -> {
                    if (item.getFoodItem() != null) {
                        item.getFoodItem().getName(); // Force initialization
                    }
                });
            }

            // Add the fully initialized order to our list
            createdOrders.add(freshOrder);

            // Generate a bill for this order
            billService.generateBillForOrder(freshOrder.getId());
        }

        return createdOrders;
    }

    // For backward compatibility, keep the existing createOrder method but have it
    // call the new one
    @Transactional
    public Order createOrder(Long userId, OrderRequestDTO orderRequest) {
        List<Order> orders = createOrdersFromMultipleRestaurants(userId, orderRequest);
        return orders.isEmpty() ? null : orders.get(0);
    }

    /**
     * Update an existing order
     */
    public Order updateOrder(Long id, Long userId, UpdateOrderRequestDTO updateRequest) {
        Order order = getOrderById(id, userId);

        // Update order status if provided
        if (updateRequest.getStatus() != null) {
            order.setStatus(updateRequest.getStatus());
        }

        // Update delivery address if provided
        if (updateRequest.getDeliveryAddressId() != null) {
            Address address = addressRepository.findById(updateRequest.getDeliveryAddressId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Address", "id", updateRequest.getDeliveryAddressId()));

            // Verify address belongs to the user
            if (!address.getUser().getId().equals(userId)) {
                throw new UnauthorizedException("You don't have permission to use this address");
            }

            order.setDeliveryAddress(address);
        }

        // Update order items if provided
        if (updateRequest.getOrderItems() != null && !updateRequest.getOrderItems().isEmpty()) {
            // Get the restaurant from the current order
            Restaurant restaurant = order.getRestaurant();

            // Clear existing order items
            orderItemRepository.deleteByOrderId(order.getId());

            // Add new order items
            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderItemRequestDTO itemRequest : updateRequest.getOrderItems()) {
                FoodItem foodItem = foodItemRepository.findById(itemRequest.getFoodItemId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Food item", "id", itemRequest.getFoodItemId()));

                // Verify food item belongs to the same restaurant
                if (!foodItem.getRestaurant().getId().equals(restaurant.getId())) {
                    throw new BadRequestException("Food item must belong to the same restaurant as the order");
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setFoodItem(foodItem);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setItemPrice(foodItem.getPrice());

                orderItems.add(orderItem);
            }

            orderItemRepository.saveAll(orderItems);
        }

        // Update order timestamp
        order.setOrderTime(LocalDateTime.now());

        // Save the order
        order = orderRepository.save(order);

        // Retrieve the fully loaded order
        return getOrderById(order.getId(), userId);
    }

    /**
     * Delete an order
     */
    public void deleteOrder(Long id, Long userId) {
        Order order = getOrderById(id, userId);

        // Only allow cancellation of non-delivered orders
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot delete an order that has already been delivered");
        }

        orderRepository.delete(order);
    }
}
