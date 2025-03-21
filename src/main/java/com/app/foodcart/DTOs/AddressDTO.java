package com.app.foodcart.DTOs;

import lombok.Data;
import com.app.foodcart.entities.Address;

@Data
public class AddressDTO {
    private Long id;
    private Long userId;
    private String locality;
    private String houseNumber;
    private String building;
    private String landmark;
    private String roadNumber;
    private String street;
    private String city;
    private String state;
    private String zipCode;

    public AddressDTO(Address address) {
        this.id = address.getId();
        this.userId = address.getUser() != null ? address.getUser().getId() : null;
        this.locality = address.getLocality();
        this.houseNumber = address.getHouseNumber();
        this.building = address.getBuilding();
        this.landmark = address.getLandmark();
        this.roadNumber = address.getRoadNumber();
        this.street = address.getStreet();
        this.city = address.getCity();
        this.state = address.getState();
        this.zipCode = address.getZipCode();
    }
}