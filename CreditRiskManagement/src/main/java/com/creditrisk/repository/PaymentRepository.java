package com.creditrisk.repository;

import com.creditrisk.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository
        extends JpaRepository<Payment, Integer> {

    List<Payment> findAllByOrderByPaymentDateDesc();
}