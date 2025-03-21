package com.app.foodcart.services.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import com.app.foodcart.entities.User;
import com.app.foodcart.exceptions.BadRequestException;
import com.app.foodcart.exceptions.DuplicateResourceException;
import com.app.foodcart.exceptions.ResourceNotFoundException;
import com.app.foodcart.repositories.UserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Async("taskExecutor")
    public CompletableFuture<User> createUserAsync(User user) {
        // Check if user with same email already exists
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }

        // Check if user with same phone number already exists
        if (user.getPhoneNumber() != null) {
            userRepository.findByPhoneNumber(user.getPhoneNumber()).ifPresent(u -> {
                throw new DuplicateResourceException("User", "phone number", user.getPhoneNumber());
            });
        }

        user.setCreatedTime(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return CompletableFuture.completedFuture(userRepository.save(user));
    }

    // Keeping synchronous method for backward compatibility
    public User createUser(User user) {
        // Check if user with same email already exists
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }

        // Check if user with same phone number already exists
        if (user.getPhoneNumber() != null) {
            userRepository.findByPhoneNumber(user.getPhoneNumber()).ifPresent(u -> {
                throw new DuplicateResourceException("User", "phone number", user.getPhoneNumber());
            });
        }

        user.setCreatedTime(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Async("taskExecutor")
    public CompletableFuture<User> updateUserAsync(Long id, User user) {
        return CompletableFuture.supplyAsync(() -> {
            User existingUser = getUserById(id);

            // Check if email is already used by another user
            if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
                User userWithSameEmail = userRepository.findByEmail(user.getEmail());
                if (userWithSameEmail != null) {
                    throw new DuplicateResourceException("User", "email", user.getEmail());
                }
            }

            // Check if phone number is already used by another user
            if (user.getPhoneNumber() != null && !user.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
                userRepository.findByPhoneNumber(user.getPhoneNumber()).ifPresent(u -> {
                    throw new DuplicateResourceException("User", "phone number", user.getPhoneNumber());
                });
            }

            if (user.getName() != null) {
                existingUser.setName(user.getName());
            }

            if (user.getEmail() != null) {
                existingUser.setEmail(user.getEmail());
            }

            if (user.getPassword() != null) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            if (user.getPhoneNumber() != null) {
                existingUser.setPhoneNumber(user.getPhoneNumber());
            }

            return userRepository.save(existingUser);
        });
    }

    // Keeping synchronous method for backward compatibility
    public User updateUser(Long id, User user) {
        try {
            return updateUserAsync(id, user).get();
        } catch (Exception e) {
            if (e.getCause() instanceof ResourceNotFoundException) {
                throw (ResourceNotFoundException) e.getCause();
            } else if (e.getCause() instanceof DuplicateResourceException) {
                throw (DuplicateResourceException) e.getCause();
            }
            throw new BadRequestException("Error updating user: " + e.getMessage());
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        // Check if user exists
        getUserById(id); // Will throw ResourceNotFoundException if not found
        userRepository.deleteById(id);
    }
}