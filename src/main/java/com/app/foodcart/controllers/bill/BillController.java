package com.app.foodcart.controllers.bill;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bills")
public class BillController {

    @GetMapping("/{id}")
    public String getBillById(@PathVariable Long id) {
        return "Bill details for ID: " + id;
    }

    @PostMapping
    public String createBill() {
        return "Bill created";
    }

    @PutMapping("/{id}")
    public String updateBill(@PathVariable Long id) {
        return "Bill updated for ID: " + id;
    }

    @DeleteMapping("/{id}")
    public String deleteBill(@PathVariable Long id) {
        return "Bill deleted for ID: " + id;
    }
}