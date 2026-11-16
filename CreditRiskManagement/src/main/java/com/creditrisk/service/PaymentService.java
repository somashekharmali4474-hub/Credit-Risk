
package com.creditrisk.service;

import com.creditrisk.entity.Customer;
import com.creditrisk.entity.Payment;
import com.creditrisk.repository.CustomerRepository;
import com.creditrisk.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository) {

        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Payment addPayment(Payment payment) {

        // Find customer
        if (payment.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }

        Customer customer = customerRepository.findById(
                payment.getCustomerId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Customer not found"));

        // Validate amount
        if (payment.getAmount() == null ||
                payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero");
        }

        if (payment.getPaymentDate() == null) {
            throw new IllegalArgumentException(
                    "Payment date is required");
        }

        // Validate payment method
        if (payment.getPaymentMethod() == null ||
                !List.of("CASH", "UPI", "BANK_TRANSFER", "CARD")
                        .contains(payment.getPaymentMethod().toUpperCase())) {
            throw new IllegalArgumentException(
                    "Invalid payment method");
        }

        // Validate transaction type
        if (payment.getTransactionType() == null) {
            throw new IllegalArgumentException(
                    "Transaction type is required");
        }

        String type = payment.getTransactionType().toUpperCase();

        if (!List.of("CREDIT", "REPAYMENT").contains(type)) {
            throw new IllegalArgumentException(
                    "Transaction type must be CREDIT or REPAYMENT");
        }

        BigDecimal pending = customer.getTotalPendingAmount();

        if (pending == null) {
            pending = BigDecimal.ZERO;
        }

        // CREDIT: Increase customer pending amount
        if (type.equals("CREDIT")) {

            pending = pending.add(payment.getAmount());

            payment.setStatus("PENDING");
        }

        // REPAYMENT: Decrease customer pending amount
        else {

            if (payment.getAmount().compareTo(pending) > 0) {
                throw new IllegalArgumentException(
                        "Repayment cannot be greater than pending amount. Current pending: "
                                + pending);
            }

            pending = pending.subtract(payment.getAmount());

            payment.setStatus("PAID");
        }

        // Update customer balance
        customer.setTotalPendingAmount(pending);

        // Calculate risk
        customer.setRiskLevel(calculateRisk(
                pending,
                customer.getCreditLimit()
        ));

        customerRepository.save(customer);

        // Save transaction
        payment.setTransactionType(type);
        payment.setPaymentMethod(
                payment.getPaymentMethod().toUpperCase()
        );

        return paymentRepository.save(payment);
    }

    // Automatic customer risk calculation
    private String calculateRisk(
            BigDecimal pending,
            BigDecimal creditLimit) {

        if (pending.compareTo(BigDecimal.ZERO) == 0) {
            return "LOW";
        }

        if (creditLimit == null ||
                creditLimit.compareTo(BigDecimal.ZERO) <= 0) {
            return "CRITICAL";
        }

        BigDecimal percentage = pending.multiply(
                BigDecimal.valueOf(100)
        ).divide(
                creditLimit,
                2,
                java.math.RoundingMode.HALF_UP
        );

        if (percentage.compareTo(BigDecimal.valueOf(50)) < 0) {
            return "LOW";
        }

        if (percentage.compareTo(BigDecimal.valueOf(80)) < 0) {
            return "MEDIUM";
        }

        if (percentage.compareTo(BigDecimal.valueOf(100)) < 0) {
            return "HIGH";
        }

        return "CRITICAL";
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentDateDesc();
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Payment not found"));
    }
}
