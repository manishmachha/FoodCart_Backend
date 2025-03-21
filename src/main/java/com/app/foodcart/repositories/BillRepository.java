package com.app.foodcart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.foodcart.entities.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
