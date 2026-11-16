package com.creditrisk.controller;

import com.creditrisk.entity.Payment;
import com.creditrisk.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Add a new payment
    @PostMapping
    public ResponseEntity<?> addPayment(@RequestBody Payment payment) {
        try {
            Payment savedPayment = paymentService.addPayment(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(paymentService.getPaymentById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}