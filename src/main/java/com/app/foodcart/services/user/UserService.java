package com.app.foodcart.services.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
import com.app.foodcart.entities.User;
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
        user.setIsActive(true);
        return CompletableFuture.completedFuture(userRepository.save(user));
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

            if (user.getRole() != null) {
                existingUser.setRole(user.getRole());
            }

            if (user.getIsActive() != null) { // ✅ Check for null
                existingUser.setIsActive(user.getIsActive());
            }

            return userRepository.save(existingUser);
        });
    }

    @Async("taskExecutor")
    public CompletableFuture<List<User>> getUsersByIdsAsync(List<Long> ids) {
        // Create a list of CompletableFutures, each fetching a user by ID in parallel
        List<CompletableFuture<User>> futures = ids.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> getUserById(id))) // Fetch each user in parallel
                .toList();

        // Combine all CompletableFutures into one CompletableFuture<List<User>>
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join) // Join all futures to get the results
                        .toList());
    }

    public List<User> getUsersByIds(List<Long> ids) {
        return ids.stream()
                .map(this::getUserById) // Use the existing getUserById method
                .toList();
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