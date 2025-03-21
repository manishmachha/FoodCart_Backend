package com.app.foodcart.DTOs.requests;

import lombok.Data;

@Data
public class AddressRequestDTO {
    private String locality;
    private String houseNumber;
    private String building;
    private String landmark;
    private String roadNumber;
    private String street;
    private String city;
    private String state;
    private String zipCode;
}