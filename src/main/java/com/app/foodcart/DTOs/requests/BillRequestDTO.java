package com.app.foodcart.DTOs.requests;

import lombok.Data;
import com.app.foodcart.entities.enums.PaymentMethod;

import java.math.BigDecimal;

@Data
public class BillRequestDTO {
    private Long orderId;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
}