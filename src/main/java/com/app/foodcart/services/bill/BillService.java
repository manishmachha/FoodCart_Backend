package com.app.foodcart.services.bill;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.foodcart.entities.Bill;
import com.app.foodcart.repositories.BillRepository;

@Service
@Transactional
public class BillService {
    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Bill generateBill(Bill bill) {
        return billRepository.save(bill);
    }

    public Bill getBillById(Long id) {
        return billRepository.findById(id).orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }
}