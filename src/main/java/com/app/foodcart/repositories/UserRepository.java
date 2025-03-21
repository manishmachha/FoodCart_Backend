package com.app.foodcart.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.foodcart.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
