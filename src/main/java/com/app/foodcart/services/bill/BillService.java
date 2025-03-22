package com.app.foodcart.services.bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.foodcart.entities.Bill;
import com.app.foodcart.entities.Order;
import com.app.foodcart.entities.OrderItem;
import com.app.foodcart.entities.enums.PaymentMethod;
import com.app.foodcart.exceptions.ResourceNotFoundException;
import com.app.foodcart.repositories.BillRepository;
import com.app.foodcart.repositories.OrderRepository;

@Service
@Transactional
public class BillService {
    private final BillRepository billRepository;

    @Autowired
    private OrderRepository orderRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Bill generateBill(Bill bill) {
        return billRepository.save(bill);
    }

    /**
     * Generate a bill for an order
     * 
     * @param orderId The ID of the order to generate a bill for
     * @return The generated bill
     */
    @Transactional
    public Bill generateBillForOrder(Long orderId) {
        // Check if bill already exists for this order
        Optional<Bill> existingBill = billRepository.findByOrderId(orderId);
        if (existingBill.isPresent()) {
            return existingBill.get();
        }

        // Get the order with all its items
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Calculate total amount from order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                BigDecimal itemTotal = item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        // Create new bill
        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setTotalAmount(totalAmount);
        bill.setPaymentMethod(PaymentMethod.CASH); // Default payment method
        bill.setPaid(false);
        bill.setCreatedTime(LocalDateTime.now());

        return billRepository.save(bill);
    }

    /**
     * Generate bills for multiple orders
     * 
     * @param orderIds List of order IDs to generate bills for
     * @return List of generated bills
     */
    @Transactional
    public List<Bill> generateBillsForOrders(List<Long> orderIds) {
        return orderIds.stream()
                .map(this::generateBillForOrder)
                .toList();
    }

    public Bill getBillById(Long id) {
        return billRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }
}