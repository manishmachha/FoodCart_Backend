package com.app.foodcart.DTOs.requests;

import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;
    private Boolean isActive;
}