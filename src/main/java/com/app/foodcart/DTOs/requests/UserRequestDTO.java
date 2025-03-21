package com.app.foodcart.DTOs.requests;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
}