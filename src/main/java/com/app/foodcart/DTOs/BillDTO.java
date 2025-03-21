package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.Bill;
import com.app.foodcart.entities.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillDTO {
    private Long id;
    private Long orderId;
    private Long userId;
    private String userName;
    private Long restaurantId;
    private String restaurantName;
    private BigDecimal totalAmount;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private boolean paid;

    public BillDTO(Bill bill) {
        this.id = bill.getId();

        if (bill.getOrder() != null) {
            this.orderId = bill.getOrder().getId();

            if (bill.getOrder().getUser() != null) {
                this.userId = bill.getOrder().getUser().getId();
                this.userName = bill.getOrder().getUser().getName();
            }

            if (bill.getOrder().getRestaurant() != null) {
                this.restaurantId = bill.getOrder().getRestaurant().getId();
                this.restaurantName = bill.getOrder().getRestaurant().getName();
            }
        }

        this.totalAmount = bill.getTotalAmount();
        this.paymentDate = bill.getPaymentDate();
        this.paymentMethod = bill.getPaymentMethod();
        this.paid = bill.isPaid();
    }
}