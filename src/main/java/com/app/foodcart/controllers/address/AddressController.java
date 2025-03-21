package com.app.foodcart.controllers.address;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    @GetMapping("/{id}")
    public String getAddressById(@PathVariable Long id) {
        return "Address details for ID: " + id;
    }

    @PostMapping
    public String createAddress() {
        return "Address created";
    }

    @PutMapping("/{id}")
    public String updateAddress(@PathVariable Long id) {
        return "Address updated for ID: " + id;
    }

    @DeleteMapping("/{id}")
    public String deleteAddress(@PathVariable Long id) {
        return "Address deleted for ID: " + id;
    }
}