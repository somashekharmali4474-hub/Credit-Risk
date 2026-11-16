
package com.creditrisk.service;

import com.creditrisk.entity.Customer;
import com.creditrisk.entity.DeletedCustomer;

import com.creditrisk.repository.CustomerRepository;
import com.creditrisk.repository.DeletedCustomerRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final DeletedCustomerRepository deletedRepository;

    public CustomerService(
            CustomerRepository repository,
            DeletedCustomerRepository deletedRepository) {

        this.repository = repository;
        this.deletedRepository = deletedRepository;
    }

    // Add Customer
    public Customer addCustomer(Customer customer) {
        customer.setStatus("ACTIVE");
        return repository.save(customer);
    }

    // Get All Active Customers
    public List<Customer> getAllCustomers() {
        return repository.findByStatusIgnoreCase("ACTIVE");
    }

    // Get Customer By ID
    public Customer getCustomerById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found"));
    }

    // Update Customer
    public Customer updateCustomer(Integer id, Customer updatedCustomer) {

        Customer customer = getCustomerById(id);

        customer.setCustomerName(updatedCustomer.getCustomerName());
        customer.setEmail(updatedCustomer.getEmail());
        customer.setPhone(updatedCustomer.getPhone());
        customer.setAddress(updatedCustomer.getAddress());
        customer.setAnnualIncome(updatedCustomer.getAnnualIncome());
        customer.setCreditLimit(updatedCustomer.getCreditLimit());

        return repository.save(customer);
    }

    // Get Deleted Customers
    public List<DeletedCustomer> getDeletedCustomers() {
        return deletedRepository.findAll();
    }

    // Move Customer to Deleted Customers Table
    public void deleteCustomer(Integer id) {

        Customer customer = getCustomerById(id);

        DeletedCustomer deleted = new DeletedCustomer();

        deleted.setCustomerId(customer.getCustomerId());
        deleted.setCustomerName(customer.getCustomerName());
        deleted.setEmail(customer.getEmail());
        deleted.setPhone(customer.getPhone());
        deleted.setAddress(customer.getAddress());
        deleted.setAnnualIncome(customer.getAnnualIncome());
        deleted.setCreditLimit(customer.getCreditLimit());
        deleted.setStatus("DELETED");
        deleted.setCreatedAt(customer.getCreatedAt());
        deleted.setDeletedAt(java.time.LocalDateTime.now());

        deletedRepository.save(deleted);

        repository.delete(customer);
    }

    // Restore Customer
    public Customer restoreCustomer(Integer deletedId) {

        DeletedCustomer deleted = deletedRepository.findById(deletedId)
                .orElseThrow(() ->
                        new RuntimeException("Deleted customer not found"));

        Customer customer = new Customer();

        customer.setCustomerName(deleted.getCustomerName());
        customer.setEmail(deleted.getEmail());
        customer.setPhone(deleted.getPhone());
        customer.setAddress(deleted.getAddress());
        customer.setAnnualIncome(deleted.getAnnualIncome());
        customer.setCreditLimit(deleted.getCreditLimit());
        customer.setStatus("ACTIVE");
        customer.setCreatedAt(deleted.getCreatedAt());

        Customer restored = repository.save(customer);

        deletedRepository.delete(deleted);

        return restored;
    }

    // Permanently Delete Customer
    public void permanentlyDeleteCustomer(Integer deletedId) {

        DeletedCustomer deleted = deletedRepository.findById(deletedId)
                .orElseThrow(() ->
                        new RuntimeException("Deleted customer not found"));

        deletedRepository.delete(deleted);
    }
}
