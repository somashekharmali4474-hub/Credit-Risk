package com.creditrisk.repository;

import com.creditrisk.entity.DeletedCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedCustomerRepository
        extends JpaRepository<DeletedCustomer, Integer> {
}