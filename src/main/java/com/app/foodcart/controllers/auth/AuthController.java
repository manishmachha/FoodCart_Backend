package com.app.foodcart.controllers.auth;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.foodcart.DTOs.ApiResponse;
import com.app.foodcart.DTOs.LoginResponseDTO;
import com.app.foodcart.DTOs.requests.LoginRequestDTO;
import com.app.foodcart.authUtils.JwtUtil;
import com.app.foodcart.entities.User;
import com.app.foodcart.exceptions.BadRequestException;
import com.app.foodcart.exceptions.ResourceNotFoundException;
import com.app.foodcart.exceptions.UnauthorizedException;
import com.app.foodcart.repositories.UserRepository;
import com.app.foodcart.services.tokenBlacklist.BlackListService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BlackListService blackListService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<ApiResponse<LoginResponseDTO>>> login(
            @RequestBody LoginRequestDTO loginRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String jwt = jwtUtil.generateToken(userDetails);
                User user = userRepository.findByEmail(loginRequest.getEmail());

                if (user == null) {
                    throw new ResourceNotFoundException("User", "email", loginRequest.getEmail());
                }

                LoginResponseDTO loginData = new LoginResponseDTO(
                        jwt,
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        null); // Remove message from DTO as it will be in ApiResponse

                ApiResponse<LoginResponseDTO> response = ApiResponse.success(
                        "Login successful", loginData);

                return ResponseEntity.ok(response);
            } catch (AuthenticationException e) {
                // This will be caught by the global exception handler
                throw new UnauthorizedException("Authentication failed: Invalid email or password");
            }
        });
    }

    @PostMapping("/logout")
    public CompletableFuture<ResponseEntity<ApiResponse<Object>>> logout(@RequestBody Map<String, String> tokenObj) {
        String token = tokenObj.get("token");

        if (token == null) {
            // This will be caught by the global exception handler
            throw new BadRequestException("No token provided");
        }

        return blackListService.blacklistTokenAsync(token)
                .thenApply(v -> {
                    SecurityContextHolder.clearContext();
                    ApiResponse<Object> successResponse = ApiResponse.success(
                            "Logout successful", null);
                    return ResponseEntity.ok(successResponse);
                })
                .exceptionally(ex -> {
                    // For async exceptions, we need to return a response
                    // since the global exception handler won't catch this
                    ApiResponse<Object> errorResponse = new ApiResponse<>();
                    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    errorResponse.setMessage("Logout failed: " + ex.getMessage());
                    errorResponse.setData(null);

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(errorResponse);
                });
    }
}
