package com.app.foodcart.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.foodcart.entities.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
    /**
     * Find a bill by order ID
     * 
     * @param orderId Order ID
     * @return Optional of Bill
     */
    @Query("SELECT b FROM Bill b WHERE b.order.id = :orderId")
    Optional<Bill> findByOrderId(@Param("orderId") Long orderId);
}
