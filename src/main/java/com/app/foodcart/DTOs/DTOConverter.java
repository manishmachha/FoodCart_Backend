package com.app.foodcart.DTOs;

import com.app.foodcart.entities.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting entities to DTOs
 */
public class DTOConverter {

    public static UserDTO convertToDTO(User user) {
        return new UserDTO(user);
    }

    public static List<UserDTO> convertToUserDTOList(List<User> users) {
        return users.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public static RestaurantDTO convertToDTO(Restaurant restaurant) {
        return new RestaurantDTO(restaurant);
    }

    public static List<RestaurantDTO> convertToRestaurantDTOList(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantDTO::new)
                .collect(Collectors.toList());
    }

    public static FoodItemDTO convertToDTO(FoodItem foodItem) {
        return new FoodItemDTO(foodItem);
    }

    public static List<FoodItemDTO> convertToFoodItemDTOList(List<FoodItem> foodItems) {
        return foodItems.stream()
                .map(FoodItemDTO::new)
                .collect(Collectors.toList());
    }

    public static OrderDTO convertToDTO(Order order) {
        return new OrderDTO(order);
    }

    public static List<OrderDTO> convertToOrderDTOList(List<Order> orders) {
        return orders.stream()
                .map(OrderDTO::new)
                .collect(Collectors.toList());
    }

    public static OrderItemDTO convertToDTO(OrderItem orderItem) {
        return new OrderItemDTO(orderItem);
    }

    public static List<OrderItemDTO> convertToOrderItemDTOList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());
    }

    public static BillDTO convertToDTO(Bill bill) {
        return new BillDTO(bill);
    }

    public static List<BillDTO> convertToListBillDTO(List<Bill> bills) {
        return bills.stream()
                .map(BillDTO::new)
                .collect(Collectors.toList());
    }

    public static AddressDTO convertToDTO(Address address) {
        return new AddressDTO(address);
    }

    public static List<AddressDTO> convertToAddressDTOList(List<Address> addresses) {
        return addresses.stream()
                .map(AddressDTO::new)
                .collect(Collectors.toList());
    }

    public static RatingDTO convertToDTO(Rating rating) {
        return new RatingDTO(rating);
    }

    public static List<RatingDTO> convertToRatingDTOList(List<Rating> ratings) {
        return ratings.stream()
                .map(RatingDTO::new)
                .collect(Collectors.toList());
    }

    public static CartDTO convertToDTO(Cart cart) {
        return new CartDTO(cart);
    }

    public static CartItemDTO convertToDTO(CartItem cartItem) {
        return new CartItemDTO(cartItem);
    }

    public static List<CartItemDTO> convertToCartItemDTOList(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(CartItemDTO::new)
                .collect(Collectors.toList());
    }
}