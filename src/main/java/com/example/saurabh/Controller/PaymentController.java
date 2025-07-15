package com.example.saurabh.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.saurabh.entity.Payment;
import com.example.saurabh.entity.PaymentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/save")
    public String savePayment(@RequestBody Payment payment) {
        paymentRepository.save(payment);
        return "Payment saved successfully!";
    }
    
    
    @GetMapping("/getpin")
    public Integer MethodName() {
         return (int)(Math.random() * 9000) + 1000;
    }
    
}
