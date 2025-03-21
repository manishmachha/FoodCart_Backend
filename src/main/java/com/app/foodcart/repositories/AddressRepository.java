package com.app.foodcart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.foodcart.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
