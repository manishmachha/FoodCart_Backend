package com.app.foodcart.controllers.bill;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.foodcart.DTOs.ApiResponse;
import com.app.foodcart.DTOs.BillDTO;
import com.app.foodcart.entities.Bill;
import com.app.foodcart.services.bill.BillService;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BillDTO>> getBillById(@PathVariable Long id) {
        Bill bill = billService.getBillById(id);
        BillDTO billDTO = new BillDTO(bill);

        ApiResponse<BillDTO> response = ApiResponse.success("Bill retrieved successfully", billDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BillDTO>>> getAllBills() {
        List<Bill> bills = billService.getAllBills();
        List<BillDTO> billDTOs = bills.stream().map(BillDTO::new).toList();

        ApiResponse<List<BillDTO>> response = ApiResponse.success("Bills retrieved successfully", billDTOs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<BillDTO>> getBillByOrderId(@PathVariable Long orderId) {
        Bill bill = billService.generateBillForOrder(orderId);
        BillDTO billDTO = new BillDTO(bill);

        ApiResponse<BillDTO> response = ApiResponse.success("Bill retrieved successfully", billDTO);
        return ResponseEntity.ok(response);
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