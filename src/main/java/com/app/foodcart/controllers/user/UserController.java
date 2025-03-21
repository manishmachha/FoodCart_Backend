package com.app.foodcart.controllers.user;

import org.springframework.web.bind.annotation.*;

import com.app.foodcart.DTOs.ApiResponse;
import com.app.foodcart.DTOs.UserDTO;
import com.app.foodcart.DTOs.requests.UserRequestDTO;
import com.app.foodcart.entities.User;
import com.app.foodcart.services.user.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
        @Autowired
        private UserService userService;

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
                UserDTO userDTO = new UserDTO(userService.getUserById(id));
                ApiResponse<UserDTO> response = ApiResponse.success(
                                String.format("User with ID %d found", id), userDTO);
                return ResponseEntity.ok(response);
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
                List<UserDTO> users = userService.getAllUsers().stream()
                                .map(UserDTO::new)
                                .collect(Collectors.toList());

                ApiResponse<List<UserDTO>> response = ApiResponse.success(
                                String.format("Found %d users", users.size()), users);
                return ResponseEntity.ok(response);
        }

        @PostMapping("/create")
        public CompletableFuture<ResponseEntity<ApiResponse<UserDTO>>> createUser(
                        @Valid @RequestBody UserRequestDTO userRequestDTO) {
                User user = new User();
                user.setName(userRequestDTO.getName());
                user.setEmail(userRequestDTO.getEmail());
                user.setPassword(userRequestDTO.getPassword());
                user.setPhoneNumber(userRequestDTO.getPhoneNumber());
                user.setRole("ROLE_USER"); // Default role

                return userService.createUserAsync(user)
                                .thenApply(createdUser -> {
                                        UserDTO userDTO = new UserDTO(createdUser);

                                        ApiResponse<UserDTO> response = ApiResponse.created(
                                                        "User created successfully", userDTO);
                                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
                                });
        }

        @PutMapping("/{id}")
        public CompletableFuture<ResponseEntity<ApiResponse<UserDTO>>> updateUser(
                        @PathVariable Long id,
                        @Valid @RequestBody UserRequestDTO userRequestDTO) {

                User user = new User();
                user.setName(userRequestDTO.getName());
                user.setEmail(userRequestDTO.getEmail());
                user.setPassword(userRequestDTO.getPassword());
                user.setPhoneNumber(userRequestDTO.getPhoneNumber());

                return userService.updateUserAsync(id, user)
                                .thenApply(updatedUser -> {
                                        UserDTO userDTO = new UserDTO(updatedUser);

                                        ApiResponse<UserDTO> response = ApiResponse.success(
                                                        String.format("User with ID %d updated successfully", id),
                                                        userDTO);
                                        return ResponseEntity.ok(response);
                                });
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {
                userService.deleteUser(id);

                ApiResponse<?> response = ApiResponse.success("User deleted successfully", null);
                return ResponseEntity.ok(response);
        }
}
