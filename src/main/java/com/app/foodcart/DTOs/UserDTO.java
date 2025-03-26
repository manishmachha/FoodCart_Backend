package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.User;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String createdTime;
    private String phoneNumber;
    private Boolean isActive;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdTime = user.getCreatedTime().toString();
        this.phoneNumber = user.getPhoneNumber();
        this.isActive = user.getIsActive();
    }
}